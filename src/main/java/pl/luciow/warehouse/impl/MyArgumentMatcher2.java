package pl.luciow.warehouse.impl;
import org.mockito.ArgumentMatcher;

import pl.luciow.warehouse.model.Mail;

public class MyArgumentMatcher2 extends ArgumentMatcher<Mail>{

	@Override
	public boolean matches(Object argument)  {
		Mail mail = (Mail)argument;
		return mail.getContent().equals("Success");
	}

}
