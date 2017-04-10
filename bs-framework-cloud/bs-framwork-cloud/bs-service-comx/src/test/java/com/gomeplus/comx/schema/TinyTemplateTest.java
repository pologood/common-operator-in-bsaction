package com.gomeplus.comx.schema;

import com.gomeplus.comx.context.Context;
import com.gomeplus.comx.utils.log.ComxLogger;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * Created by xue on 2/22/17.
 */
public class TinyTemplateTest {
    @Test
    public void test1() {
        Context context = new Context();
        ComxLogger logger = new ComxLogger();
        context.setLogger(logger);

        TinyTemplate tinyTemplate = new TinyTemplate("/{ref.a}/{query.t}");
        HashMap ref = new HashMap();
        ref.put("a", "str");
        ref.put("b", " ");
        HashMap vars = new HashMap();
        vars.put("ref", ref);

        TinyTemplate tinyTemplate2 = new TinyTemplate("/{ref.b}");
        assertEquals("/str/", tinyTemplate.render(vars, context, true));
        assertEquals("/+", tinyTemplate2.render(vars, context, true));
        assertEquals("/ ", tinyTemplate2.render(vars, context, false));
    }
}
