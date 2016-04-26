/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.luciow.warehouse.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import pl.luciow.warehouse.PaymentProcessor;
import pl.luciow.warehouse.model.Payment;
import pl.luciow.warehouse.util.PaymentValidator;
import pl.luciow.warehouse.util.ValidatorException;

/**
 *
 * @author Mariusz
 */
@RunWith(MockitoJUnitRunner.class)
public class PaymentServiceImplTest {
	
	private static final Logger log4j = LogManager.getLogger(PaymentServiceImplTest.class.getName());
	
	@InjectMocks
	private PaymentServiceImpl paymentServiceImpl = new PaymentServiceImpl();

	@Mock
	private PaymentProcessor paymentProcessor;

	@Mock
	private PaymentValidator paymentValidator;

	@SuppressWarnings("unchecked")
	//@Test(expected = ValidatorException.class)
	@Test
	public void processPaymentTest() throws Exception {

		Mockito.doAnswer(new Answer<Void>() {
			public Void answer(InvocationOnMock invocation) {
				List<String> errors = (List<String>) invocation.getArguments()[1];
				errors.add("Some Error");
				invocation.getArguments()[1] = errors;
				return null;
			}
		}).when(paymentValidator).validate(Mockito.any(Payment.class), Mockito.any(List.class));

		Payment payment = new Payment();
		
		try {
			paymentServiceImpl.processPayment(payment);
			Mockito.verify(paymentProcessor, Mockito.times(1)).processPayment(payment);
			log4j.info("One call of the processPayment method!");
		} catch (ValidatorException e) {
			Mockito.verify(paymentProcessor, Mockito.times(0)).processPayment(payment);
			log4j.info("No call of the processPayment method!");
		//	throw new ValidatorException(new ArrayList());
		}
	
	}
	
}
