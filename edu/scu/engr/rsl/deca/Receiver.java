package edu.scu.engr.rsl.deca;

import java.io.IOException;
import java.io.OutputStream;
import com.rbnb.sapi.*;

public class Receiver extends Thread {
	private Sink internalSink;
	private OutputStream client;

	public Receiver( String dataTurbine, String botName , OutputStream out ) throws Exception {
		internalSink = new Sink( );
		internalSink.OpenRBNBConnection( dataTurbine, botName + "-sink" );
		ChannelMap watchList = new ChannelMap( );
		watchList.Add( "controller/" +  botName  );
		internalSink.Subscribe( watchList );
		client = out;
	}

	@Override
	public void run ( ) {
		while ( true ) {
			// The argument to Fetch is read timeout in ms. If there is no data,
			// it will eventually time out and return an empty channel map. I
			// actually have no idea what circumstances it will throw an
			// SAPIException under, because killing rbnb causes it to throw
			// java.lang.IllegalStateException.
			try {
				ChannelMap getmap = internalSink.Fetch( 1000 );
				if ( getmap.GetChannelList().length > 0 ) {
					// String dataString = new String( getmap.GetDataAsByteArray( 0 )[0] );
					// System.out.println( getmap.GetName( 0 ) + ": " + dataString );
					byte[] message = getmap.GetDataAsByteArray( 0 )[0];
					try {
						client.write( message );
					} catch ( IOException error ) {
						System.out.println( "Client write error: " + error.getMessage( ) );
						return;
					}
				}
			} catch ( SAPIException error ) {
				System.out.println( "SAPIException: " + error.getMessage( ) );
				return;
			}
		}
	}
}