package org.richardliao.spring.data;

import java.util.List;

import org.richardliao.spring.Spittle;

public interface SpittleRepository {

	List<Spittle> findSpittles(long max, int count);
	Spittle findOne(long id);
}
