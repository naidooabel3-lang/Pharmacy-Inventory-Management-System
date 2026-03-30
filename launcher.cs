using System;
using System.Diagnostics;
using System.IO;

class Launcher
{
    static void Main()
    {
        // .bat file name
        string batchFile = "run.bat";

        if (!File.Exists(batchFile))
        {
             // Optional: Error if run.bat is missing
             return;
        }

        Process p = new Process();
        p.StartInfo.FileName = batchFile;
        
        p.StartInfo.WindowStyle = ProcessWindowStyle.Hidden;
        p.StartInfo.CreateNoWindow = true;
        p.StartInfo.UseShellExecute = false;
		
       //start the process        
        p.Start();
    }
}
