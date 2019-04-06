package com.project.LifeAndDeath;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.LifeAndDeath.Controller.Board;
import com.project.LifeAndDeath.Controller.Record;
import com.project.LifeAndDeath.Controller.Rule;
import com.project.LifeAndDeath.Controller.StaticLifeAndDeathAnalysisProcess;
import com.project.LifeAndDeath.DB.Service;
import com.project.LifeAndDeath.Model.Quiz;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	
	Service service;
	
	public enum Color{
		Empty,Black,White
	}
	
    ArrayList<Integer[]> updateList=new ArrayList<>();
	
	Board board=new Board(updateList,0);
	Rule rule=new Rule(board);
	Record record = new Record();
	Quiz quiz = new Quiz();
	ArrayList tileList = new ArrayList();
	StaticLifeAndDeathAnalysisProcess staticLifeAndDeathAnalysisProcess =new StaticLifeAndDeathAnalysisProcess(board);
	boolean confirm;
	

	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	

	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String board(HttpSession session) {
		logger.info("Start");
		
		MLModel m = new MLModel();
		try {
			System.out.println(m.model1());
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		m=null;
		System.gc();
		
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		
		rule.setRecord(record);
		board.clear();
		rule.clear();
		record.record.clear();
		confirm=false;
		quiz.setHashMap((HashMap)board.hashMap.clone());
		
		return "Board";
	}
	
	@ResponseBody
	@RequestMapping(value = "/putStone", method = RequestMethod.GET)// 돌 올려 놓을때
	public HashMap putStone(@RequestParam int Num ,int color,int mode) {
		HashMap returnResult=new HashMap<>();
		updateList.clear();
		if(mode==-1) {
			System.out.println("Edit mode");
			if (color == 0) {
				board.update(Num);
			}
			else {
				rule.killOwnAction(Num,color);
			}
			quiz.setHashMap((HashMap)board.getBoardMap().clone());
			rule.reset();
		}
		else if(mode==0||mode==1) {	/*PlayWithComputerMode*/
			HashMap tmp = board.getBoardMap();
          
            if (rule.goRule(Num, color)){
            	returnResult.put("hashMap", tmp);
            
            }
		}
		else if(mode==2||mode==3){  /*helpMeAI*/
			HashMap tmp = board.getBoardMap();
			if (rule.goRule(Num, color)){
				returnResult.put("hashMap", tmp);
			}
			
//			staticLifeAndDeathAnalysisProcess.upDateResultValue();
//	        ArrayList list = staticLifeAndDeathAnalysisProcess.getGroupInfo();
//	        
//	        StaticLifeAndDeathAnalysisProcess.LifeAndDeath lifeAndDeath;
//	        String resultArea="";
//	        HashMap groupNameMap = addLNDLabelAndMakeGroupName();
//	        
//	        for (int i = 0; i < list.size(); i++){
//	            lifeAndDeath = staticLifeAndDeathAnalysisProcess.getLND((int)list.get(i), color);
//	            
//	            resultArea+=groupNameMap.get(list.get(i))+": "+lifeAndDeath.name()
//                + "Life point: " + staticLifeAndDeathAnalysisProcess.getLifePointByGroupNo((int)list.get(i))+"\n";
//   
//	           }
	       
		}else {	/*clickSelfTestMode*/
			System.out.println("Play mode");
                rule.goRule(Num, color);
  
        }
		returnResult.put("list", updateList);
		
		return returnResult;
		
		
	}
	
	@RequestMapping(value = "/reset", method = RequestMethod.GET)
	public String reset() {
		logger.info("Reset");

		
		rule.setRecord(record);
		board.clear();
		rule.clear();
		record.record.clear();
		confirm=false;
		quiz.setHashMap((HashMap)board.hashMap.clone());
		staticLifeAndDeathAnalysisProcess.reset();
		return "Board";
	}
	
	@ResponseBody
	@RequestMapping(value = "/undo", method = RequestMethod.GET)
	public ArrayList<Integer[]> undo(@RequestParam int mode) {
		updateList.clear();
		if (rule.getMove_Count() == 0)
            return updateList;
        if (mode == 4||mode==5){
             rule.undo(1, quiz);
         } else if (mode == 0||mode==1){
             rule.undo(2, quiz);
         }
        return updateList;
	}
	
	@ResponseBody
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public void login(@RequestParam String name, String email,HttpSession session) {
//		int no=service.checkId(email);
//		if(no!=0) {
//			session.setAttribute("no", no);
//		}
//		else {
//			service.signUp(name,email);
//			session.setAttribute("no", service.checkId(email));
//		}
	}
	
	@ResponseBody
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public void logout(HttpSession session) {
		session.removeAttribute("no");
	}
	
	private HashMap addLNDLabelAndMakeGroupName(){
        ArrayList list = staticLifeAndDeathAnalysisProcess.getGroupInfo();
        HashMap groupName = new HashMap();
//        for (int i =0; i < list.size(); i++){
//            char c = (char) ((char)i + 65);
//            groupName.put(list.get(i), c);
//        }
//        HashMap hashMap = staticLifeAndDeathAnalysisProcess.getResultMap();
//        for (int i = 0; i < 361; i++){
//            if (hashMap.get(i) != null){
//                FrameLayout tile = (FrameLayout) findViewById(i);
//                TextView textView = new TextView(this.getApplicationContext());
//                textView.setText(Character.toString((char)groupName.get(hashMap.get(i))));
//                textView.setGravity(Gravity.CENTER);
//                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
//                if ((int)hashMap.get(i) >= 0){
//                    textView.setTextColor(Color.WHITE);
//                } else {
//                    textView.setTextColor(Color.BLACK);
//                }
//                tile.addView(textView, layoutParams);
//            }
//        }
        return groupName;
    }
	
	 private void sendRequest(HashMap h,String stone){
//	        HashMap<String,String> sendMap = new HashMap<String,String>();
//	        for(int i=0;i<361;i++){
//	            if(h.get(i)!=null)
//	                sendMap.put(i+"",h.get(i)+"");
//	        }
//	        sendMap.put("stone",stone);
//	        try{
//	            Gson gson = new Gson();
//	            Stones data = gson.fromJson(networkTask.execute(sendMap).get().toString(), Stones.class);
//	            int tmp = data.getResult();
//	            if(stone.equals("black"))
//	                rule.goRule(tmp, Color.Black.ordinal());
//	            else
//	                rule.goRule(tmp,Color.White.ordinal());
//	        }catch (Exception e){
//	            e.printStackTrace();
//	        }
	    }
	

}