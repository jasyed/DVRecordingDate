# DVRecordingDate

## Name

DVRecordingDate -- gets the recording date and time of DV file(s)

## Synopsis

    java DVRecordingDate [-h | --help | -rename] file ... 

## Description

Gets the recording date and time of the first frame from raw DV files. Optionally renames files using the convention used by iMovie 08. If Mac OS X Developer Tools are installed, the creation date of files are set to the recording date.

This code was written to address the problem [Imported iMovie HD project clips have incorrect capture date](http://discussions.apple.com/thread.jspa?threadID=1344628) in iMovie 08.

To import with a correct recording date, iMovie 08 uses the following naming convention for DV clips:

    clip-2008-07-28 09;30;00.dv

This is also described at [How to change the date for DV event footage](http://imovie08.blogspot.com/2007/09/how-to-change-date-for-dv-event-footage.html).

## Options

-h \-\-help

> Print this help page

-rename

> In addition to displaying the recording date, rename each file with its recording date and time as required by iMovie 08. If the Mac OS X Developer Tools are installed, the file creation date for the file is set to the recording date.

## Usage

First, check that this program can correctly read date information from the input DV files. In a terminal window type:

    java DVRecordingDate MyiMovieHDproject.iMovieProject/Media/*.dv

and the recording dates of the project's DV files should be displayed. If this program cannot read the date information, error messages will be displayed.

To rename the files (and if the developer tools are installed, change the file creation dates), use the -rename option. NB: Only work on a copy of the `.dv` files from an iMovie project as renaming a project's DV files will break it.

When iMovie 08 is not running, the `.dv` files can be imported directly into the iMovie events folder thus:

    mkdir ~/Movies/iMovie\ Events.localized/MyEventName
    cp MyiMovieHDproject.iMovieProject/Media/*.dv ~/Movies/iMovie\ Events.localized/MyEventName/
    java DVRecordingDate -rename ~/Movies/iMovie\ Events.localized/MyEventName/*.dv

Upon the next launch of iMovie 08, a set of thumbnails should be created for the new event, and the event appear in the event library.

## Compatibility

This program has only been tested with PAL DV files imported by iMovie HD, but NTSC DV files should also work. This program was designed to run under Mac OS X, but except for setting the file creation date, it should work on other platforms with a recent installation of Java.

## Caveats

Mac OS X Developer Tools are an optional install, so `SetFile` might not be available to change the file creation date of DV files. This error is not reported, but should not prevent correct importation into iMovie 08.

If two clips were recorded at the same time (to the accuracy of a second) then the renaming operation may fail when renaming the second file.

Only the first frame of the DV file is used to find the recording date. If this frame is corrupted, then date information cannot be read.

## Thanks

There does not appear to be any official documentation for the DV format available free of charge. Many thanks to the authors of [Kino](http://www.kinodv.org) whose source code provided the information for parsing DV date information, particularly `GetSSYBPack`, `GetRecordingDate` in `frame.cc` and `Pack` in `frame.h` from the Kino 1.3.1 sourcecode.

## Author

This is a specialised adaption of a snippet of code from Kino (see thanks section), translated from the original C to Java with the additional functionality to rename files in the format required by iMovie 08, by Jameel Syed.

## Warranty

This is free software and is provided "as is". There is NO warranty; not even for MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. All trademarks are the property of their respective owners.