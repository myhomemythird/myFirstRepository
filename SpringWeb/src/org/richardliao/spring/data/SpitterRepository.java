package org.richardliao.spring.data;

import org.richardliao.spring.Spitter;

public interface SpitterRepository {

	Spitter save(Spitter spitter);
	Spitter findByUsername(String username);
}
