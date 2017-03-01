#!/bin/bash
BUILD_ID=DONTKILLME
set -e
# ENV代表环境  $1表示JOB_NAME $2表示VERSION
ENV=prd

export JAVA_HOME=/usr/local/jdk1.7.0_75
export PATH=$JAVA_HOME/bin:$PATH

LOG_LEVEL=DEBUG
SERVICE_BASE_PATH=/gomeo2o
SERVICE_NAME=${1}
SERVICE_BASE_URL=http://10.125.136.44
VERSION=${1}-${2}.tar.gz
SCRIPT_PATH=/gomeo2o/script
TMP=temp


##停止服务
function stopService(){
	if [ ! -e ${SERVICE_BASE_PATH}/${SERVICE_NAME}/history ]
	then
		mkdir -p ${SERVICE_BASE_PATH}/${SERVICE_NAME}/history
	fi
	if [ ! -e ${SERVICE_BASE_PATH}/${SERVICE_NAME}/logs ]
        then
                mkdir -p ${SERVICE_BASE_PATH}/${SERVICE_NAME}/logs
        fi
	if [ ! -e ${SERVICE_BASE_PATH}/${SERVICE_NAME}/temp ]
        then
                mkdir -p ${SERVICE_BASE_PATH}/${SERVICE_NAME}/temp
        fi
	if [ -e ${SERVICE_BASE_PATH}/${SERVICE_NAME}/bin ]
	then
		${SERVICE_BASE_PATH}/${SERVICE_NAME}/bin/venus-service stop
	fi
}
function mklogback(){
	wget ${SERVICE_BASE_URL}/logback/logback_tmplate.xml
	mv logback_tmplate.xml logback.xml
	sed -i "s/venus-app-name/${SERVICE_NAME}/g" logback.xml
	sed -i "s/DEBUG/${LOG_LEVEL}/g" logback.xml
}
function changeprdwrapper(){
	sed -i 's/wrapper.java.initmemory=256M/wrapper.java.initmemory=4096M/g' wrapper.conf
	sed -i 's/wrapper.java.maxmemory=256M/wrapper.java.maxmemory=4096M/g' wrapper.conf
}
##发布服务
function deploy(){
	cd ${SERVICE_BASE_PATH}/${SERVICE_NAME}/temp
	rm -rf *
	#从nginx下载服务
	wget ${SERVICE_BASE_URL}/${SERVICE_NAME}/${VERSION}
	tar zxvf ./*.tar.gz
	rm -rf venus-service/logs
	mv ./venus-service/*  ../
	chmod +x ${SERVICE_BASE_PATH}/${SERVICE_NAME}/bin/*
	cd ../etc
	#为服务添加配置文件，配置文件分环境各不相同
	mv -f ${SERVICE_BASE_PATH}/${TMP}/application-${ENV}.properties application-${ENV}.properties
	if [ "${SERVICE_NAME}" == "bs-admin-web-vote" ]
    	then
            mv -f ${SERVICE_BASE_PATH}/${TMP}/tfs.properties app.properties                              
    	fi
	sed -i "s/Dspring.profiles.active=dev/Dspring.profiles.active=${ENV}/g" wrapper.conf
	if [ "${ENV}" == "prd" ]
	then 
	      changeprdwrapper
	fi
	# 日志配置
	mklogback
}


##备份
function backup(){
	cd  ${SERVICE_BASE_PATH}/${SERVICE_NAME}
	if [ -e ${SERVICE_BASE_PATH}/${SERVICE_NAME}/bin ]
	then	
		TIMESTAMP=`date "+%Y-%m-%d_%H-%M-%S"`
		tar -czvf ${SERVICE_NAME}_${TIMESTAMP}.tar.gz  ./bin ./etc ./lib 
		mv ./*_*.tar.gz ./history/
		rm -rf ${SERVICE_BASE_PATH}/${SERVICE_NAME}/bin
		rm -rf ${SERVICE_BASE_PATH}/${SERVICE_NAME}/lib
		rm -rf ${SERVICE_BASE_PATH}/${SERVICE_NAME}/etc
	fi
}


# 启动服务
function startService(){

	${SERVICE_BASE_PATH}/${SERVICE_NAME}/bin/venus-service start
	echo ">>>正在启动dubbor,请稍等 ... "
	sleep 10
	tail -50 ${SERVICE_BASE_PATH}/${SERVICE_NAME}/logs/wrapper.log
}

function main(){
	stopService
	backup
	deploy
	startService
}
main
