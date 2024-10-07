package irc;
import java.io.Serializable;

import jvn.Operation;
public interface SentenceAnnotation extends Serializable{
	
	@Operation(name = "read")
	String read();

	@Operation(name = "write")
	void write(String s);
}
