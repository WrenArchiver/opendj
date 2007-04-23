/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at
 * trunk/opends/resource/legal-notices/OpenDS.LICENSE
 * or https://OpenDS.dev.java.net/OpenDS.LICENSE.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at
 * trunk/opends/resource/legal-notices/OpenDS.LICENSE.  If applicable,
 * add the following below this CDDL HEADER, with the fields enclosed
 * by brackets "[]" replaced with your own identifying information:
 *      Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 *
 *      Portions Copyright 2006-2007 Sun Microsystems, Inc.
 */
package org.opends.statuspanel;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.opends.quicksetup.util.Utils;
import org.opends.quicksetup.Installation;
import org.opends.statuspanel.i18n.ResourceProvider;

/**
 * This class is called by the control panel command lines to launch the
 * control panel of the Directory Server.
 *
 */
public class StatusPanelLauncher
{
  /**
   * The main method which is called by the control panel command lines.
   * @param args the arguments passed by the command lines.
   */
  public static void main(String[] args)
  {
    boolean printUsage = false;
    if ((args != null) && (args.length > 4))
    {
      printUsage = true;
    }
    for (int i=0; i<args.length; i++)
    {
      if (args[i].equalsIgnoreCase("-H") ||
          args[i].equalsIgnoreCase("--help") ||
          args[i].equalsIgnoreCase("-?"))
      {
        printUsage = true;
      }
    }
    if (printUsage)
    {
      printUsage(System.out);
      System.exit(1);

    } else
    {
      int exitCode = launchGuiStatusPanel(args);
      if (exitCode != 0)
      {
        System.err.println(getMsg("status-panel-launcher-gui-launch-failed"));
        System.exit(exitCode);
      }
    }
  }

  /**
   * Launches the graphical status panel. It is launched in a
   * different thread that the main thread because if we have a problem with the
   * graphical system (for instance the DISPLAY environment variable is not
   * correctly set) the native libraries will call exit. However if we launch
   * this from another thread, the thread will just be killed.
   *
   * This code also assumes that if the call to SplashWindow.main worked (and
   * the splash screen was displayed) we will never get out of it (we will call
   * a System.exit() when we close the graphical status dialog).
   *
   * @params String[] args the arguments used to call the SplashWindow main
   *         method
   * @return 0 if everything worked fine, or 1 if we could not display properly
   *         the SplashWindow.
   */
  private static int launchGuiStatusPanel(final String[] args)
  {
    final int[] returnValue = { -1 };
    Thread t = new Thread(new Runnable()
    {
      public void run()
      {
        // Setup MacOSX native menu bar before AWT is loaded.
        Utils.setMacOSXMenuBar(getMsg("statuspanel-dialog-title"));
        SplashScreen.main(args);
        returnValue[0] = 0;
      }
    });
    /*
     * This is done to avoid displaying the stack that might occur if there are
     * problems with the display environment.
     */
    PrintStream printStream = System.err;
    //System.setErr(new EmptyPrintStream());
    t.start();
    try
    {
      t.join();
    }
    catch (InterruptedException ie)
    {
      /* An error occurred, so the return value will be -1.  We got nothing to
      do with this exception. */
    }
    System.setErr(printStream);
    return returnValue[0];
  }

  private static void printUsage(PrintStream stream)
  {
    String arg;
    if (Utils.isWindows())
    {
      arg = Installation.WINDOWS_STATUSPANEL_FILE_NAME;
    } else
    {
      arg = Installation.UNIX_STATUSPANEL_FILE_NAME;
    }
    /*
     * This is required because the usage message contains '{' characters that
     * mess up the MessageFormat.format method.
     */
    String msg = getMsg("status-panel-launcher-usage");
    msg = msg.replace("{0}", arg);
    stream.println(msg);
  }

  /**
   * The following three methods are just commodity methods to get localized
   * messages.
   */
  private static String getMsg(String key)
  {
    return org.opends.server.util.StaticUtils.wrapText(getI18n().getMsg(key),
        Utils.getCommandLineMaxLineWidth());
  }

  private static ResourceProvider getI18n()
  {
    return ResourceProvider.getInstance();
  }

  /**
   * This class is used to avoid displaying the error message related to display
   * problems that we might have when trying to display the SplashWindow.
   *
   */
  static class EmptyPrintStream extends PrintStream
  {
    /**
     * Default constructor.
     *
     */
    public EmptyPrintStream()
    {
      super(new ByteArrayOutputStream(), true);
    }

    /**
     * {@inheritDoc}
     */
    public void println(String msg)
    {
    }
  }
}

