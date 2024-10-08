package irc;
import java.io.Serializable;

import jvn.Operation;

public interface SentenceInterface extends Serializable {
	
	@Operation(name = "write")
	public void write(String text);
	
	@Operation(name = "read")
	public String read();
}
