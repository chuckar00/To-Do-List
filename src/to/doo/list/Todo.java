package to.doo.list;



public class Todo {
	private boolean done;
	private String text;
	
	public Todo(String text){
		done = false;
		this.text = text;
	}
	
	public boolean isDone(){
		return done;
	}
	
	public String getText(){
		return text;
	}
	
	public void setDone(boolean done){
		this.done = done;
	}
	
	public void setText(String text){
		this.text = text;
	}
	
}
