package com.cheney.template;

import com.cheney.utils.Message;
import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * Created by cheny on 2017/9/10.
 */
@Component("flushMessageDirective")
public class FlushMessageDirective implements TemplateDirectiveModel {

    public final static String FLUSH_MESSAGE_ATTRIBUTE_NAME = FlushMessageDirective.class.getName() + ".FLUSH_MESSAGE";

    @Override
    public void execute(Environment environment, Map map, TemplateModel[] templateModels, TemplateDirectiveBody body) throws TemplateException, IOException {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        Message message = (Message) requestAttributes.getAttribute(FLUSH_MESSAGE_ATTRIBUTE_NAME, RequestAttributes.SCOPE_REQUEST);
        if (message != null) {
            Writer out = environment.getOut();
            out.write("$.message(\"" + message.getType() + "\"," + "\"" + message.getcontent() + "\")");
        }
    }
}
