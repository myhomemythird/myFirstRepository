package org.richardliao.kafka.springboot;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import java.util.concurrent.CountDownLatch;
//import java.util.logging.Logger;

public class Listener {

	public Listener() {
		// TODO Auto-generated constructor stub
	}

	//protected Logger logger = Logger.getLogger(Listener.class.getName());

	public CountDownLatch getCountDownLatch1() {
	    System.out.println("In getCountDownLatch1()");
		return countDownLatch1;
	}

	private CountDownLatch countDownLatch1 = new CountDownLatch(1);

	@KafkaListener(topics = "test")
	public void listen(ConsumerRecord<?, ?> record) {
		//logger.info("Received message: " + record.toString());
		System.out.println("Received message: " + record);
		//countDownLatch1.countDown();
	}
}
