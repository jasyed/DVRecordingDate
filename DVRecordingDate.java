/**
 * DVRecordingDate -- gets the recording date and time of DV file(s)
 * Copyright (C) 2008 Jameel Syed <jasyed@acm.org>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Version 1.0 - 2008-08-28 - Initial release
 */

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DVRecordingDate
{
	private static String HELP_STRING= "\n"+
	"NAME\n"+
	"     DVRecordingDate -- gets the recording date and time of DV file(s)\n"+
	"\n"+
	"SYNOPSIS\n"+
	"     java DVRecordingDate [-h | --help | -rename] file ...\n"+
	"\n"+
	"DESCRIPTION\n"+
	"     Gets the recording date and time of the first frame from raw DV files.\n"+
	"     Optionally renames files using the convention used by iMovie 08.\n"+
	"     If Mac OS X Developer Tools are installed, the creation date of files are\n"+
	"     set to the recording date.\n"+
	"\n"+
	"     This code was written to address the following problem in iMovie 08:\n"+
	"       Imported iMovie HD project clips have incorrect capture date\n"+
	"       http://discussions.apple.com/thread.jspa?threadID=1344628\n"+
	"\n"+
	"     To import with a correct recording date, iMovie 08 uses the following\n"+
	"     naming convention for DV clips:\n"+
	"       clip-2008-07-28 09;30;00.dv\n"+
	"     This is also described at:\n"+
	"       http://imovie08.blogspot.com/2007/09/how-to-change-date-for-dv-event-footage.html\n"+
	"\n"+
	"OPTIONS\n"+
	"     -h --help\n"+
	"       Print this help page\n"+
	"\n"+
	"     -rename\n"+
	"       In addition to displaying the recording date, rename each file with\n"+
	"       its recording date and time as required by iMovie 08. If the Mac OS X\n"+
	"       Developer Tools are installed, the file creation date for the file is set\n"+
	"       to the recording date.\n"+
	"\n"+
	"USAGE\n"+
	"     First, check that this program can correctly read date information from\n"+
	"     the input DV files. In a terminal window type:\n"+
	"       java DVRecordingDate MyiMovieHDproject.iMovieProject/Media/*.dv\n"+
	"     and the recording dates of the project's DV files should be displayed.\n"+
	"     If this program cannot read the date information, error messages will be\n"+
	"     displayed.\n"+
	"\n"+
	"     To rename the files (and if the developer tools are installed, change\n"+
	"     the file creation dates), use the -rename option.\n"+
	"     NB: Only work on a copy of the .dv files from an iMovie project as renaming\n"+
	"     a project's DV files will break it.\n"+
	"\n"+
	"     When iMovie 08 is not running, the .dv files can be imported directly into\n"+
	"     the iMovie events folder thus:\n"+	
	"       mkdir ~/Movies/iMovie\\ Events.localized/MyEventName\n"+
	"       cp MyiMovieHDproject.iMovieProject/Media/*.dv \\\n"+
	"          ~/Movies/iMovie\\ Events.localized/MyEventName/\n"+
	"       java DVRecordingDate -rename \\\n"+
	"            ~/Movies/iMovie\\ Events.localized/MyEventName/*.dv\n"+
	"     (the \\ character is used here to break the long command over two lines)\n"+
	"     Upon the next launch of iMovie 08, a set of thumbnails should be created\n"+
	"     for the new event, and the event appear in the event library.\n"+
	"\n"+
	"COMPATIBILITY\n"+
	"     This program has only been tested with PAL DV files imported by iMovie HD,\n"+
	"     but NTSC DV files should also work. This program was designed to run under\n"+
	"     Mac OS X, but except for setting the file creation date, it should work on\n"+
	"     other platforms with a recent installation of Java.\n"+
	"\n"+
	"CAVEATS\n"+
	"     Mac OS X Developer Tools are an optional install, so SetFile might not be\n"+
	"     available to change the file creation date of DV files. This error is not\n"+
	"     reported, but should not prevent correct importation into iMovie 08.\n"+
	"\n"+
	"     If two clips were recorded at the same time (to the accuracy of a second)\n"+
	"     then the renaming operation may fail when renaming the second file.\n"+
	"\n"+
	"     Only the first frame of the DV file is used to find the recording date.\n"+
	"     If this frame is corrupted, then date information cannot be read.\n"+
	"\n"+
	"THANKS\n"+
	"     There does not appear to be any official documentation for the DV format\n"+
	"     available free of charge. Many thanks to the authors of\n"+
	"     Kino (http://www.kinodv.org) whose source code provided the information\n"+
	"     for parsing DV date information, particularly GetSSYBPack, GetRecordingDate\n"+
	"     in frame.cc and Pack in frame.h from the Kino 1.3.1 sourcecode.\n"+
	"\n"+
	"AUTHOR\n"+
	"     This is a specialised adaption of a snippet of code from Kino (see thanks\n"+
	"     section), translated from the original C to Java with the additional\n"+
	"     functionality to rename files in the format required by iMovie 08, \n"+
	"     by Jameel Syed.\n"+
	"\n"+
	"WARRANTY\n"+
	"     This is free software and is provided \"as is\".\n"+
	"     There is NO warranty; not even for MERCHANTABILITY or FITNESS FOR A\n"+
	"     PARTICULAR PURPOSE.\n"+
	"     All trademarks are the property of their respective owners.\n\n";


	// This size isn't precise, but it's enough to get the job done :-)
	static final int FILE_HEADER_LENGTH= 512;


	public static void main(String[] args) throws Exception
	{
		if( args.length == 0 )
		{
			System.err.println(
				"usage: java DVRecordingDate [-h | --help | -rename] file ...");
			System.exit(1);
		}

		if( args[0].equals("-h") || args[0].equals("--help") )
		{
			System.out.print(HELP_STRING);
			System.exit(0);
		}

		// Either displaying or renaming files
		boolean rename= args[0].equals("-rename");
		int fileArgStartIndex= (rename) ? 1 : 0;
		
		if( rename )
			System.out.println("Renaming file(s) from, to:");
				
		for( int i=fileArgStartIndex; i<args.length; i++ )
			examineDVFile(args[i], rename);
	}


	private static void examineDVFile(String filename, boolean rename) throws Exception
	{
		File f= new File(filename);

		if( !f.exists() )
		{
			System.err.println("File "+f+" does not exist");
			return;
		}

		if( f.isDirectory() )
		{
			System.err.println("File "+f+" is a directory: "+
				"specify path for single file(s) or use *.dv");
			return;
		}
		
		byte[] b= new byte[FILE_HEADER_LENGTH];

		FileInputStream fis= new FileInputStream(f);
		fis.read(b);
		fis.close();

		// The DV file header includes 3 'SSYB subcode packets' each of 5 bytes.
		// The first byte of each packet is an ID; we are interested in those
		// packets whose IDs are 'b' and 'c'.
		// For simplicity I have used specific start offsets for these 3 packets
		// although it's probably better to search for these packets by ID.
		// All my PAL DV files conform to these specifications but YMMV.
		
		int PACKET_A_OFFSET= 0x1c5; // value should be 'a' = 0x61 (unused)
		int PACKET_B_OFFSET= 0x1ca; // value should be 'b' = 0x62
		int PACKET_C_OFFSET= 0x1cf; // value should be 'c' = 0x63
		
		// Check to see whether DV file conforms to these assumptions
		if( (char)b[PACKET_A_OFFSET]=='a' &&
		    (char)b[PACKET_B_OFFSET]=='b' &&
		    (char)b[PACKET_C_OFFSET]=='c' )
		{
			// This part is adapted from GetRecordingDate in Kino

			// Get raw date and time values from file header
			byte rawDay= b[ PACKET_B_OFFSET+2 ];
			byte rawMonth= b[ PACKET_B_OFFSET+3 ];
			byte rawYear= b[ PACKET_B_OFFSET+4 ];
			byte rawSec= b[ PACKET_C_OFFSET+2 ];
			byte rawMin= b[ PACKET_C_OFFSET+3 ];
			byte rawHour= b[ PACKET_C_OFFSET+4 ];

			// Post process raw date and time into something printable
			int day= ( rawDay & 0xf ) + 10 * ( ( rawDay >>> 4 ) & 0x3 );
			int month= ( rawMonth & 0xf ) + 10 * ( ( rawMonth >>> 4 ) & 0x1 );
			int year= ( rawYear & 0xf ) + 10 * ( ( rawYear >>> 4 ) & 0xf );
			int sec= ( rawSec & 0xf ) + 10 * ( ( rawSec >>> 4 ) & 0x7 );
			int min= ( rawMin & 0xf ) + 10 * ( ( rawMin >>> 4 ) & 0x7 );
			int hrs= ( rawHour & 0xf ) + 10 * ( ( rawHour >>> 4 ) & 0x3 );

			if( year<25 )
				year+= 2000;
			else
				year+= 1900;

			Calendar recordDate= Calendar.getInstance();
			recordDate.set(year, month-1, day, hrs, min, sec);

			// Define date formats for different purposes

			// When used to just display dates
			SimpleDateFormat isoStyle=
				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			// clip-2008-07-28 09;30;00.dv
			SimpleDateFormat iMovieStyle=
				new SimpleDateFormat("yyyy-MM-dd HH;mm;ss");

			// "12/25/2002 15:34"
			SimpleDateFormat setFileStyle=
				new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

			if( rename )
			{
				String newFilename=
					"clip-"+iMovieStyle.format(recordDate.getTime())+".dv";
				File newFile= new File(f.getParentFile(),newFilename);

				if( !newFile.exists() && f.renameTo(newFile) )
					System.out.println(f+"\t"+newFilename );
				else
					System.err.println("Couldn't rename "+f+" to "+newFilename);

				// This part is not vital, but is the icing on the cake :-)
				// Fail silently since requires developer tools to be installed
				try
				{
					String[] cmdarray= new String[4];
					cmdarray[0]= "/Developer/Tools/SetFile";
					cmdarray[1]= "-d";
					cmdarray[2]= setFileStyle.format(recordDate.getTime());
					cmdarray[3]= newFilename;

					Runtime.getRuntime().exec(cmdarray, null, f.getParentFile());
				}
				catch( Exception failSilently )
				{
				}
			}
			else
			{
				System.out.println(f+"\t"+isoStyle.format(recordDate.getTime()));
			}
		}
		else
		{
			System.err.println("Cannot parse the DV header of file "+f);
		}
	}
}