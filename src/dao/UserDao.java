package dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import tools.PageInformation;
import tools.Tool;
import tools.WebProperties;
import bean.User;

public class UserDao {
	public boolean hasUser(User user,DatabaseDao databaseDao) throws SQLException{
		String sql="select * from user where name='"+user.getName()+"'";
		databaseDao.query(sql);
		while(databaseDao.next()){
			return true;
		}
		return false;
	}
	
	public Integer register(User user,DatabaseDao databaseDao) throws SQLException{
		user.setHeadIconUrl("\\"+WebProperties.config.getString("projectName")
				+WebProperties.config.getString("headIconFileDefault"));//默认头像
		String sql="insert into user(type,name,password,enable,headIconUrl) values('"+
				user.getType()+"','"+user.getName()+"','"+
				user.getPassword()+"','"+user.getEnable()+"','"+
				user.getHeadIconUrl().replace("\\", "/")+"')";
		return databaseDao.update(sql);
	}
	
	public Integer login(User user) throws SQLException, Exception{
		DatabaseDao databaseDao=new DatabaseDao();
		String sql="select * from user where name='" + user.getName()+
						"' and password='"+ user.getPassword()+"'";
		databaseDao.query(sql);
		while(databaseDao.next()){
			String enable=databaseDao.getString("enable");
			if( ("use").equals(enable)  ){
				user.setType(databaseDao.getString("type"));
				user.setUserId(databaseDao.getInt("userId"));
				user.setHeadIconUrl(databaseDao.getString("headIconUrl"));
				user.setRegisterDate(databaseDao.getTimestamp("registerDate"));
				return 1;
			}			
			return 0;
		}
		return -1;
	}	
	
	public List<User> getOnePage(PageInformation pageInformation,DatabaseDao databaseDao) throws SQLException{
		List<User> users=new ArrayList<User>(); 
		String sqlCount=Tool.getSql(pageInformation,"count");
		Integer allRecordCount=databaseDao.getCount(sqlCount);
		Tool.setPageInformation(allRecordCount, pageInformation);
		
		String sqlSelect=Tool.getSql(pageInformation,"select");
		databaseDao.query(sqlSelect);
		while (databaseDao.next()) {
			User user=new User();
			user.setEnable(databaseDao.getString("enable"));
			user.setName(databaseDao.getString("name"));
			user.setRegisterDate(databaseDao.getTimestamp("registerDate"));
			user.setType(databaseDao.getString("type"));
			user.setUserId(databaseDao.getInt("userId"));
			users.add(user);	
		}		
		return users;
	}	

	
	public Integer changeEnable(String id,DatabaseDao databaseDao)throws SQLException{
		String sql = "select * from user where userId in ("+id+")";
		databaseDao.query(sql);
		while (databaseDao.next()) {
			String enable=databaseDao.getString("enable");
			if("use".equals(enable))
				enable="stop";
			else
				enable="use";
			sql = "update user set enable='"+enable+"' where userId in ("+id+")";
			databaseDao.update(sql);
			return 1;
		}		
		return 0;
	}
	
	
	public Integer deletes(String ids,DatabaseDao databaseDao)throws SQLException{
		if(ids!=null && ids.length()>0){
			String sql = "delete from user where userId in ("+ids+")";
			return databaseDao.update(sql);
		}else
			return -1;
	}	
	
	public Integer updateHeadIcon(User user,DatabaseDao databaseDao)throws SQLException{//
		String sql = "update user set headIconUrl='"+user.getHeadIconUrl()+
				"' where userId ="+user.getUserId().toString();
		return databaseDao.update(sql.replace("\\", "/"));

	}	
}
