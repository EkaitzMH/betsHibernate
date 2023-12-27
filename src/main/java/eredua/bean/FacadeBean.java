package eredua.bean;

import eredua.BLFacade;
import eredua.BLFacadeImplementation;
import nagusia.HibernateDataAccess;

public class FacadeBean {
	private static FacadeBean singleton = new FacadeBean( );
	
	private static BLFacade facadeInterface;
	
	private FacadeBean(){
		try { facadeInterface=new BLFacadeImplementation(new HibernateDataAccess()); }
		catch (Exception e) {
			System.out.println("FacadeBean: negozioaren logika sortzean errorea: "+e.getMessage());
		}
	}
	
	public static BLFacade getBusinessLogic( ) {
		return facadeInterface;
	}
}