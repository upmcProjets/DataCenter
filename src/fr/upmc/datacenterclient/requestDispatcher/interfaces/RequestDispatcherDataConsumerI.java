package fr.upmc.datacenterclient.requestDispatcher.interfaces;


/**
 * @author chelbi
 *
 */
public interface RequestDispatcherDataConsumerI {


	public void			acceptRequestDispatcherDynamicData(
			String					computerURI,
			RequestDispatcherDynamicStateI	currentDynamicState
			) throws Exception ;


}
