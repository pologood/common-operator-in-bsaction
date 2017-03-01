#!/bin/bash

BASE_PATH=$PWD
GIT_URL=`git remote -v | grep fetch | awk '{print $2}'`
PROJECT_NAME=${JOB_NAME}
NGINX_FILE_PATH=/gomeo2o/project/${PROJECT_NAME}
WORK_BRANCH=${SUBFIELD}
PROJECT_VERSION=""
TAR_VERSION=""

TEMP=/tmp/mvn_pkg-`date +%Y%m%d%H%M%S`.log
echo "">${TEMP}
FTP_URL=http://10.125.201.33:8181

function check_property_snapshot()
{
	echo "正在检查依赖...."
	verions_arr=$(mvn versions:display-property-updates |fgrep venus|fgrep [INFO]|fgrep -v Building|awk '{ print $4}')
	for  v_arr in $verions_arr
	do
		if [[ "${v_arr}" =~ -SNAPSHOT$ ]]
		then
			echo "=^【-!-】^=:发布release版本不能有snapshot的依赖，请修改后重试==="
			echo "##################################BEGIN####################################"
			mvn versions:display-property-updates |fgrep venus|fgrep SNAPSHOT | grep -v Building
			echo "###################################END#####################################"
			exit 1
		fi
	done	
	echo "========依赖检查通过=========="
}

function pushPkgToRemote()
{
    #TAR_NAME=${PROJECT_NAME}-${TAR_VERSION}.tar.gz
    set -e
    echo "--------maven--打包成功，准备tar.gz,并放入nginx文件服务器目录-------"
    cd ${BASE_PATH}/target/
    #tar zcvf ${TAR_NAME} venus-service/
    sudo mkdir -p ${NGINX_FILE_PATH}/${HISTORY_PATH}
    sudo mv -f ${PROJECT_NAME}-${TAR_VERSION}.jar ${NGINX_FILE_PATH}
    echo "====最后:==把服务发布包上传到nginx文件服务${FTP_URL}/${PROJECT_NAME}">>${TEMP}
}
#获取项目版本号
function get_project_version()
{
    echo ${PROJECT_NAME}
    PROJECT_VERSION=`mvn clean|fgrep "Building ${PROJECT_NAME}"|awk '{print $NF}'`
    echo "当前pom.xml中项目快照版本为:${PROJECT_VERSION}"
    if [[ "${PROJECT_VERSION}" =~ -SNAPSHOT$ ]]
    then
            :
    else
            echo "[ERROR]:给出的打release的分支版本不带SNAPSHOT，请修改后重试"
            exit 1
    fi
    echo "====*====:原项目版本号为:${PROJECT_VERSION}--------">>${TEMP}
}
function display_property_updates()
{
	echo "=========:此版本对其他项目依赖和最新版本对比==================">>${TEMP}
	mvn versions:display-property-updates |grep venus | grep "\\$">>${TEMP}
	echo "==============================================================">>${TEMP}
}
#错误回滚
function error_rollback()
{
    echo "====出错===="
    git pull
    mvn clean
    mvn versions:set -DnewVersion=${PROJECT_VERSION} -e
    git add pom.xml
    git commit -m "恢复到打包之前的snapshot版本"
    git push origin master
    echo "===打包失败，回滚成功==="
    exit 1
}
#release打包
function release_mvn_pkg()
{
    RELEASE_VERSION=${PROJECT_VERSION%-SNAPSHOT}
    TAR_VERSION=${RELEASE_VERSION}
    echo "准备发布的release版本为：【${RELEASE_VERSION}】"
    echo "====*====:发布版本号为:${RELEASE_VERSION}-------">>${TEMP}
    git fetch origin --prune --tags
    #if [ -d ${BASE_PATH}/tags/${RELEASE_VERSION} ]
    EXIST_TAG=`git tag|grep ${RELEASE_VERSION}`
    if [ $EXIST_TAG"x" != "x" ]
    then
	    echo "==【-!-】==tags已经存在${RELEASE_VERSION}，请检查是否已有对于release版本"
    fi
    mvn versions:set -DnewVersion=${RELEASE_VERSION} -e || exit 1
    git add pom.xml
    VERSION=`git commit -m "release:${RELEASE_VERSION}"|head -n 1|awk '{print $2}'`
    GIT_VERSION=${VERSION/]/ }
    echo $GIT_VERSION
    #SVN_VERSION=`svn ci ${BASE_PATH}/${WORK_BRANCH} -m "release:${RELEASE_VERSION}" | fgrep 'Committed revision'|awk '{print $NF}'`
    if [ $? -eq 0 ]
    then
            echo "git commit success,this git version is ${GIT_VERSION}"
    else
            mvn versions:set -DnewVersion=${PROJECT_VERSION} -e
            echo "svn commit failed"
            exit 1
    fi
    #升snapshot版本号
    NEW_SNAPSHOT=`snapshot_Incr ${RELEASE_VERSION}`"-SNAPSHOT"
    echo "准备更新snapshot版本号为:${NEW_SNAPSHOT}"
    echo "====*====:开发版本被更新为:${NEW_SNAPSHOT}-------">>${TEMP}
    mvn versions:set -DnewVersion=${NEW_SNAPSHOT} -e || error_rollback
    git pull
    git add pom.xml
    git commit ${BASE_PATH} -m "${NEW_SNAPSHOT}" || error_rollback
    git push origin master

    #对上一个版本打包
    git checkout $GIT_VERSION .
    cat pom.xml
    if [[ "${PROJECT_NAME}" =~ -servlet$ ]]
    then 
	    mvn clean deploy -e -Ppkg -Dmaven.test.skip=true || error_rollback
    else    
    	mvn clean deploy -e -Dmaven.test.skip=true || error_rollback
    fi
    echo "----deploy到私服成功--------"
    echo "====*====:发版版本jar包上传私服成功:${RELEASE_VERSION}-------">>${TEMP}
    display_property_updates
    #分支提交到tags
    git tag -a ${RELEASE_VERSION} ${GIT_VERSION} -m "add tag ${RELEASE_VERSION}" || error_rollback
    git push --tags
    #svn cp --parents ${BASE_PATH}/${WORK_BRANCH} ${BASE_PATH}/tags/${RELEASE_VERSION} || error_rollback
    #svn ci ${BASE_PATH}/tags/${RELEASE_VERSION} -m "cp ${RELEASE_VERSION} to tags" || error_rollback
    echo "====*====:发版版本打tag成功-------">>${TEMP}
}
#工具函数
function snapshot_Incr()
{
        arr=()
    i=0
    var=${RELEASE_VERSION//./ }
    for element in $var
    do
            arr[(i++)]=$element
    done
    len=${#arr[@]}
    arr[$len -1]=`expr "${arr[$len-1]}" + "1"`
    vs=""
    for str in ${arr[@]}
    do
            vs=${vs}"."${str}
    done
    echo ${vs#.}
}
#快照打包
function snapshot_mvn_pkg()
{       
	TAR_VERSION=${PROJECT_VERSION}
	if [[ "${PROJECT_NAME}" =~ -servlet$ ]]
	then
		mvn deploy -e -U -Ppkg -e -Dmaven.test.skip=true || exit 1
		echo "servlet项目打包"
	else
		mvn deploy -e -U -Dmaven.test.skip=true || exit 1
	fi
	display_property_updates
	echo "====*====:SNAPSHOT版本jar包上传私服成功:${PROJECT_VERSION}-------">>${TEMP}
}

function check_already_packaged()
{
	#if [ `svn info ${BASE_PATH}/${WORK_BRANCH} | fgrep 'Last Changed Author' | awk '{print $NF}'` == 'mxsev' ]
	if [ `git log -1|grep Author|awk '{print $2}'` == 'mxsev' ]
	then
		echo "!!! 这个包应该已经打过了 !!!"
		exit 1
	fi
}

function main()
{
    get_project_version
    if [ "${PKTYPE}" == "release" ]
    then
	    check_already_packaged
	    check_property_snapshot
            release_mvn_pkg
            pushPkgToRemote ${TAR_VERSION}
    else
            snapshot_mvn_pkg
            pushPkgToRemote ${TAR_VERSION}
    fi
    cat ${TEMP}
    
    #删除日志文件
    if [ -f "${TEMP}" ]
    then
	    rm -f ${TEMP}
    fi
}

main


