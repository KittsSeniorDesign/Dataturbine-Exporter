package edu.scu.engr.rsl.deca;

import java.io.File;
import com.rbnb.sapi.SAPIException;
import com.etsy.net.JUDS;
import com.etsy.net.UnixDomainSocketServer;

public class DataturbineExporter {
	/**
	 * Sets up the send and receive threads based on arguments passed in. Expected
	 * to be run from the command line.
	 * @param arg
	 *        An array of three Strings, containing the full path to the unix
	 *        domain socket to listen on, the dataturbine server host and port,
	 *        and the name of the robot (for setting up the dataturbine source and
	 *        sink).
	 */
	public static void main(String[] arg) throws Exception {
		try {
			File danglingSock = new File( arg[0] );
			danglingSock.delete( );
		// If this fails due to permissions error, the socket constructor will
		// raise an exception, so just gobble all exceptions here.
		} catch ( Exception error ) { }

		// This constructor blocks until a client connects, which is very annoying
		// because we can't set up the shutdown hook without an initialized server.
		// If the client never connects and the program is interrupted here (e.g. by
		// sigint), the socket file won't get cleaned up, which is why we delete it
		// on startup.
		UnixDomainSocketServer server = new UnixDomainSocketServer( arg[0], JUDS.SOCK_STREAM );

		// Set up a hook to unlink the socket when the vm is exiting.
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run( ) {
				System.out.println("bye");
				if (server != null) {
					server.unlink( );
				}
			}
		});

		Sender sender;
		Receiver receiver;
		try {
			sender = new Sender( arg[1], arg[2], server.getInputStream( ) );
			receiver = new Receiver( arg[1], arg[2], server.getOutputStream( ) );
		} catch ( SAPIException error ) {
			System.out.println( "Could not connect to dataturbine." );
			return;
		}

		sender.start( );
		receiver.start( );

		sender.join( );
		receiver.join( );
	}
}
