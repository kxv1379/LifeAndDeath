package com.project.LifeAndDeath.DB;

import javax.inject.Inject;

public class ServiceImp implements Service {
	
	Dao dao;

	@Override
	public int checkId(String email) {
		// TODO Auto-generated method stub
		return dao.checkId(email);
	}

	@Override
	public void signUp(String name, String email) {
		dao.signUp(name,email);
		
	}
	
	

}
