package com.project.LifeAndDeth;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	ArrayList<Integer[]> updateList=new ArrayList<>();
	
	Board board=new Board(updateList,0);
	Rule rule=new Rule(board);
	Record record = new Record();
	ArrayList tileList = new ArrayList();
	

	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		
		rule.setRecord(record);
		board.clear();
		rule.clear();
		record.record.clear();
		
		return "home";
	}
	
	@ResponseBody
	@RequestMapping(value = "/putStone", method = RequestMethod.GET)// 돌 올려 놓을때
	public ArrayList<Integer[]> putStone(@RequestParam int Num ,int color,int mode) {
		updateList.clear();
		if(mode==-1) {
			System.out.println("Edit mode");
			if (color == 0) 
				board.update(Num);
			else {
				rule.killOwnAction(Num,color);
			}
		}
		else /*if(mode == Mode.PLAY)*/ {
			System.out.println("Play mode");
                rule.goRule(Num, color);
  
        }
		
		return updateList;
		
	}
	
	
}
