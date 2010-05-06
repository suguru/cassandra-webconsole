package net.ameba.cassandra.web.util;

import java.io.IOException;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import freemarker.core.Environment;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

/**
 * {@link FreemarkerExceptionHandler} は、 Freemarker におけるテンプレート関連例外を
 * 正しく処理するための例外ハンドラクラスです。
 * 
 * @author snamura
 */
@Component("exceptionHandler")
public class FreemarkerExceptionHandler implements TemplateExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(FreemarkerExceptionHandler.class);

	/*
	 * (non-Javadoc)
	 * @see
	 * freemarker.template.TemplateExceptionHandler#handleTemplateException(freemarker.template.TemplateException,　freemarker.core.Environment, java.io.Writer)
	 */
	public void handleTemplateException(
			TemplateException exception,
			Environment environment,
			Writer writer)
				throws TemplateException {
		try {
			if (exception.getCause().getClass() == NoSuchMessageException.class) {
				NoSuchMessageException ex = (NoSuchMessageException) exception.getCause();
				writer.write(ex.getMessage());
			} else {
				writer.write("Error: ");
				writer.write(exception.getMessage());
			}
		} catch (IOException ex) {
			log.warn(ex.getMessage(), ex);
		}
		
	}
}
