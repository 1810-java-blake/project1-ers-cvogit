package com.revature.project.one.Services;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mindrot.jbcrypt.BCrypt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.revature.project.one.Dao.UserDao;
import com.revature.project.one.Models.User;

public class UserServiceImpl implements UserService {
	private UserDao sUser = UserDao.currentImplementation;
	
	@Override
	public User findById(int pId) {
		return sUser.getUser(pId);
	}

	@Override
	public User findByJwt(String pJwt) {
		try {
		    DecodedJWT jwt = JWT.decode(pJwt);
		    Claim claim = jwt.getClaim("id");
		    return findById(claim.asInt());
		} catch (JWTDecodeException exception){
		    //Invalid token
		}
		return null;
	}

	@Override
	public List<User> findAll() {
		return sUser.getAllUsers();
	}
	
	@Override
	public List<User> findSet(int pLimit, int pOffset) {
		return sUser.getSetOfUsers(pLimit, pOffset);
	}

	@Override
	public User login(String tUsername, String tPassword) {
		// Attempt to get the user from db
		User tUser = sUser.getUser(tUsername);
		if(tUser != null) {
			if(BCrypt.checkpw(tPassword, tUser.getPassword())) {
				return tUser;
			}
		}
		return null;
	}

	@Override
	public boolean create(HttpServletRequest req, HttpServletResponse resp) {
		
		Map<String, String[]> rRequestMap = req.getParameterMap();
		
		if( rRequestMap.containsKey("username") && 
			rRequestMap.containsKey("password") && 	
			rRequestMap.containsKey("password_confirmation") && 	
			rRequestMap.containsKey("first_name") &&
			rRequestMap.containsKey("last_name") &&
			rRequestMap.containsKey("email")) {
			
			if(!req.getParameter("password").equals(req.getParameter("password_confirmation")))
				return false;
			
			User newUser = new User(0,
					req.getParameter("username"), 
					req.getParameter("password"), 
					req.getParameter("first_name"), 
					req.getParameter("last_name"), 
					req.getParameter("email"), 
					1,
					false);
			
			newUser.hashPassword();
			
			sUser.insertUser(newUser);
			return true;
		}
		return false;
	}

}
