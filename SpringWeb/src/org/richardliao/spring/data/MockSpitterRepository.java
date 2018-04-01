package org.richardliao.spring.data;

import org.richardliao.spring.Spitter;
import org.springframework.stereotype.Repository;

@Repository
public class MockSpitterRepository implements SpitterRepository {
	
	private Spitter spitter = null;

	@Override
	public Spitter save(Spitter spitter) {
		this.spitter = spitter;
		return spitter;
	}

	@Override
	public Spitter findByUsername(String username) {
		String name = this.spitter.getUsername();
		if (name.equals(username))
			return this.spitter;
		else {
			Spitter spitter = new Spitter(username + "_Mocked", "Password", "Richard", "Liao");
			return spitter;
		}
	}

}
