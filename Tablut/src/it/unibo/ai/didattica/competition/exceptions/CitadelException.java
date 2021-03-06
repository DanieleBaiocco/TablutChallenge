package it.unibo.ai.didattica.competition.exceptions;

import it.unibo.ai.didattica.competition.domain.Action;

public class CitadelException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public CitadelException(Action a)
	{
		super("Move into a citadel: "+a.toString());
	}

}
