package eredua.bean;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import eredua.BLFacade;
import dataAccess.DataAccessInterface;
import eredua.BLFacadeImplementation;
import configuration.ConfigXML;
import configuration.UtilDate;
import eredua.domeinua.Event;
import eredua.domeinua.Question;
import exceptions.EventFinished;
import exceptions.QuestionAlreadyExist;

@ManagedBean
@ViewScoped
public class LoginBean implements Serializable {
	private static final long serialVersionUID = 1L;
	transient BLFacade facadeBL;
	
	private int urtea;
	private int hilabetea;
	private int eguna;
	private Date data;
	private Event aukeratutakoGertaera;
    private List<Event> gertaerak;
    private List<Question> galderak;
    private String question;
    private float betMinimun;
    
    public LoginBean() {
    	
    }
    public Event getAukeratutakoGertaera() {
    	return aukeratutakoGertaera;
    }
    public void setAukeratutakoGertaera(Event aukeratutakoGertaera) {
        this.aukeratutakoGertaera = aukeratutakoGertaera;
    }
   
    public int getUrtea() {
        return urtea;
    }
    public void setUrtea(int urtea) {
        this.urtea= urtea;
    }
    public int getHilabetea() {
        return hilabetea;
    }
    public void setHilabetea(int hilabetea) {
        this.hilabetea= hilabetea;
    }
    public int getEguna() {
        return eguna;
    }
    public void setEguna(int eguna) {
        this.eguna= eguna;
    }
    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data= data;
    }

    public List<Event> getGertaerak() {
        return gertaerak;
    }

    public void setGertaerak(Vector<Event> gertaerak) {
        this.gertaerak = gertaerak;
    }
    public List<Question> getGalderak() {
        return galderak;
    }
    public void setGalderak(List<Question> galderak) {
        this.galderak = galderak;
    }
    public String getQuestion() {
        return this.question;
    }
    public void setQuestion(String question) {
        this.question= question;
    }
    public float getBetMinimun() {
        return this.betMinimun;
    }
    public void setBetMinimun(float betMinimun) {
        this.betMinimun=betMinimun;
    }
    
    
    public void getEvents(){
    	hilabetea= hilabetea-1;
    	this.facadeBL =FacadeBean.getBusinessLogic();
    	ConfigXML c = ConfigXML.getInstance();
    	this.data = UtilDate.newDate(urtea,hilabetea,eguna);
    	List<Event> vecevents = this.facadeBL.getEvents(this.data);
    	this.gertaerak= vecevents;
    }
    public void getQuestions(){
    	this.galderak= this.aukeratutakoGertaera.getQuestions();
    	System.out.println(this.galderak);
    }
    public void createQuestion() throws EventFinished, QuestionAlreadyExist {
    	this.facadeBL =FacadeBean.getBusinessLogic();
    	ConfigXML c = ConfigXML.getInstance();
    	facadeBL.createQuestion(aukeratutakoGertaera, question, betMinimun);
    }


    
    public String Create(){
		return "create";
	}
	public String Query(){
		return "query";
	}
}

