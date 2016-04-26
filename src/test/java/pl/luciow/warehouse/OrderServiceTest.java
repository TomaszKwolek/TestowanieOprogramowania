/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.luciow.warehouse;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.mockito.Mockito;

import pl.luciow.warehouse.impl.MyArgumentMatcher;
import pl.luciow.warehouse.impl.MyArgumentMatcher2;
import pl.luciow.warehouse.impl.OrderServiceImpl;
import pl.luciow.warehouse.impl.PaymentServiceImpl;
import pl.luciow.warehouse.impl.PaymentServiceImplTest;
import pl.luciow.warehouse.impl.WarehouseImpl;
import pl.luciow.warehouse.model.Item;
import pl.luciow.warehouse.model.Mail;
import pl.luciow.warehouse.model.NotEnoughItemsException;
import pl.luciow.warehouse.model.Order;
import pl.luciow.warehouse.model.OrderProcessException;
import pl.luciow.warehouse.model.Payment;


/**
 *
 * @author Mariusz
 */
public class OrderServiceTest {

    private OrderService orderService;
	private static final Logger log4j = LogManager.getLogger(OrderServiceTest.class.getName());

    @SuppressWarnings("unchecked")
	@Test
    public void fillOrderSuccesTest() {
    	Warehouse warehouseMock = Mockito.mock(Warehouse.class);
    	orderService= new OrderServiceImpl(null, null, warehouseMock);
    	
    	try {
			Mockito.when(warehouseMock.removeItems(Mockito.any(List.class))).thenReturn(null);
		} catch (NotEnoughItemsException e) {
			fail();
		} 
    	try {
			orderService.fillOrder(new Order());
		} catch (OrderProcessException e) {
			fail();
		}
    }

	@SuppressWarnings("unchecked")
	@Test(expected=OrderProcessException.class)
    public void fillOrderThrowTest() throws OrderProcessException {
    	Warehouse warehouseMock = Mockito.mock(Warehouse.class);
    	orderService= new OrderServiceImpl(null, null, warehouseMock);
    	
    	try {
			Mockito.when(warehouseMock.removeItems(Mockito.any(List.class))).thenThrow(new NotEnoughItemsException());
		} catch (NotEnoughItemsException e) {
		} 
			orderService.fillOrder(new Order());
    }
	
    @SuppressWarnings("unchecked")
	@Test
    public void cancelOrderTest() {
    	Warehouse warehouseMock = Mockito.mock(WarehouseImpl.class);
    	Order orderTest = new Order();
    	List<Item> items = new ArrayList<Item>();
    	
    	items.add(new Item());
    	items.add(new Item());
    	items.get(0).setName("Item_1");
    	items.get(1).setName("Item_2");
    	items.get(0).setPrice(2);
    	items.get(1).setPrice(5);
    	orderTest.setItems(items);
    	
    	Mockito.doCallRealMethod().when(warehouseMock).addItems(Mockito.any(List.class));
    	Mockito.doCallRealMethod().when(warehouseMock).getItems();
    	orderService= new OrderServiceImpl(null, null, warehouseMock);
    	
    	try {
			orderService.cancelOrder(orderTest);
		} catch (OrderProcessException e) {
			fail();
		}
    	assertEquals(warehouseMock.getItems(),items);
    }
    
    @Test
    public void processPaymentThrowTest() throws Exception {
    	PaymentService paymentServiceMock = Mockito.mock(PaymentServiceImpl.class);
    	MailService mailServiceMock = Mockito.mock(MailService.class);
    	orderService = new OrderServiceImpl(mailServiceMock, paymentServiceMock, null);
    	Payment payment = new Payment();   
    	
    	Mockito.when(paymentServiceMock.processPayment(payment)).thenThrow(new Exception());
    	
    	orderService.processPayment(new Order(), payment);
    	
    	Mockito.verify(mailServiceMock).sendMail((Mail)Mockito.argThat(new MyArgumentMatcher()));
    }
 
	@Test
    public void processPaymentSuccessTest() throws Exception {
		PaymentService paymentServiceMock = Mockito.mock(PaymentServiceImpl.class);
    	MailService mailServiceMock = Mockito.mock(MailService.class);
    	orderService = new OrderServiceImpl(mailServiceMock, paymentServiceMock, null);
    	Payment payment = new Payment();    	
    	
    	Mockito.when(paymentServiceMock.processPayment(payment)).thenReturn(1L);
    	
    	orderService.processPayment(new Order(), payment);
    	
    	Mockito.verify(mailServiceMock).sendMail((Mail)Mockito.argThat(new MyArgumentMatcher2()));
    }

}
