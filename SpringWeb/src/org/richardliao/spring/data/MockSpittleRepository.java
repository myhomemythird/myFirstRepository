package org.richardliao.spring.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.richardliao.spring.Spittle;
import org.springframework.stereotype.Repository;

@Repository
public class MockSpittleRepository implements SpittleRepository {

	@Override
	public List<Spittle> findSpittles(long max, int count) {
		
		return createSpittleList(max, count);
	}
	
	private List<Spittle> createSpittleList(long max, int count) {
		List<Spittle> spittles = new ArrayList<Spittle>();
		for (int i = 0; i < count; i++) {
			spittles.add(new Spittle("[" + max + "]Spittle " + i, new Date()));
		}
		return spittles;
	}

	@Override
	public Spittle findOne(long id) {
		Spittle spittle = new Spittle("Hello Spring MVC", new Date());
		return spittle;
	}

}
