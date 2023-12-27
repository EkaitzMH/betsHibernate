package nagusia;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import eredua.domeinua.Event;
import eredua.domeinua.Question;
import eredua.HibernateUtil;
import exceptions.QuestionAlreadyExist;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;

import configuration.ConfigXML;
import configuration.UtilDate;

public class HibernateDataAccess {
	public HibernateDataAccess(){
	}
	Session session = HibernateUtil.getSessionFactory().openSession();
	ConfigXML c=ConfigXML.getInstance();
	
	private void createAndStoreGertaera(String deskribapena, Date data) {
		 session.beginTransaction();
		 Event e = new Event();
		 e.setDescription(deskribapena);
		 e.setEventDate(data);
		 //Question q1;
		 //q1= e.addQuestion("Nork irabaziko du?", 1);
		 session.persist(e);
		 session.getTransaction().commit();
		 
		 }
	
	public void initializeDB() {
	    HibernateDataAccess e = new HibernateDataAccess();
	    open();
	    try {
	        session.beginTransaction();

	        System.out.println("Gertaeren sorkuntza:");
	        e.createAndStoreGertaera("Alaves-Osasuna", UtilDate.newDate(2024, 1, 1));
	        e.createAndStoreGertaera("Alegria-Salvatierra",UtilDate.newDate(2024, 1, 1));
	        e.createAndStoreGertaera("Rayo-Deportivo", UtilDate.newDate(2024, 1, 1));
	        System.out.println("Gertaeren zerrenda:");
	        session.getTransaction();
	        
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    } finally {
	        e.close();
	    }
	}
	
	public Question createQuestion(Event event, String question, float betMinimum) throws  QuestionAlreadyExist {
		System.out.println(">> DataAccess: createQuestion=> event= "+event+" question= "+question+" betMinimum="+betMinimum);
		System.out.println(session+" "+event);
		
		
			String hql = "FROM Evento WHERE eventNumber = :zbk";
			Query query = session.createQuery(hql);
			query.setParameter("zbk", event.getEventNumber());

			Event gert= (Event) query.uniqueResult();
			
			if (gert.DoesQuestionExists(question)) throw new QuestionAlreadyExist(ResourceBundle.getBundle("Etiquetas").getString("ErrorQueryAlreadyExist"));
			
			session.getTransaction().begin();
			Question q = gert.addQuestion(question, betMinimum);
			//db.persist(q);
			session.persist(gert); // db.persist(q) not required when CascadeType.PERSIST is added in questions property of Event class
							// @OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.PERSIST)
			session.getTransaction().commit();
			return q;
		
	}
	public List<Event> getEvents(Date date) {
		System.out.println(">> DataAccess: getEvents");
		//session.beginTransaction();
		List<Event> res = new ArrayList<Event>();
		System.out.println(date);
		try {
			session.beginTransaction();

			 String hql = "FROM Event ev WHERE ev.eventDate = :date";
	         Query query = session.createQuery(hql);
	         query.setParameter("date", date);

	         List<Event> events = query.list();
	            
	        for (Event ev : events) {
	            System.out.println(ev.toString());
	            res.add(ev);
	        }

	        session.getTransaction().commit();
	    } catch (Exception e) {
	        session.getTransaction().rollback();
	        e.printStackTrace();
	    } finally {
	        session.close();
	    }
	 	return res;
	}
	
	public List<Date> getEventsMonth(Date date) {
        System.out.println(">> DataAccess: getEventsMonth");
        List<Date> res = new ArrayList<Date>();

        Date firstDayMonthDate = UtilDate.firstDayMonth(date);
        Date lastDayMonthDate = UtilDate.lastDayMonth(date);

        try {
            // Inicia la transacción
            session.beginTransaction();

            // Utiliza HQL para la consulta
            String hql = "SELECT DISTINCT ev.eventDate FROM Event ev WHERE ev.eventDate BETWEEN :firstDate AND :lastDate";
            Query query = session.createQuery(hql);
            query.setParameter("firstDate", firstDayMonthDate);
            query.setParameter("lastDate", lastDayMonthDate);

            // Obtiene los resultados de la consulta
            List<Date> dates = query.list();

            for (Date d : dates) {
                System.out.println(d.toString());
                res.add(d);
            }

            // Hace commit de la transacción solo si hay fechas
            if (!dates.isEmpty()) {
                session.getTransaction().commit();
            }
        } catch (Exception e) {
            // Maneja excepciones, realiza rollback si es necesario
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            // Cierra la sesión en un bloque finally
            if (session.isOpen()) {
                session.close();
            }
        }

        return res;
    }
	public void open() {
        System.out.println("Opening DataAccess instance => isDatabaseLocal: " + c.isDatabaseLocal() + " getDatabBaseOpenMode: " + c.getDataBaseOpenMode());

        if (c.isDatabaseLocal()) {
            // Configuración para base de datos local
            session = HibernateUtil.getSessionFactory().openSession();
        } else {
            // Configuración para base de datos remota
            Configuration config = new Configuration();
            config.setProperty("hibernate.connection.url", "jdbc:mysql://" + c.getDatabaseNode() + ":" + c.getDatabasePort() + "/" + c.getDbFilename());
            config.setProperty("hibernate.connection.username", c.getUser());
            config.setProperty("hibernate.connection.password", c.getPassword());

            SessionFactory sessionFactory = config.buildSessionFactory();
            session = sessionFactory.openSession();
        }
    }
	
	public void close() {
        try {
            if (session != null && session.isOpen()) {
                session.close();
            }
            System.out.println("Session closed");

            SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
            if (sessionFactory != null && !sessionFactory.isClosed()) {
                sessionFactory.close();
            }
            System.out.println("SessionFactory closed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	public void emptyDatabase() {
		 try {
	            // Cerrar la sesión existente si está abierta
	            close();

	            // Eliminar los archivos de la base de datos
	            File f = new File(c.getDbFilename());
	            f.delete();

	            File f2 = new File(c.getDbFilename() + "$");
	            f2.delete();
	            
	            System.out.println("Database emptied successfully");
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		
	}
}
