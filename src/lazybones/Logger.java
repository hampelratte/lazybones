/* $Id: Logger.java,v 1.1 2005-11-23 16:41:49 hampelratte Exp $
 * 
 * Copyright (c) 2005, Henrik Niehaus & Lazy Bones development team
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of the project (Lazy Bones) nor the names of its 
 *    contributors may be used to endorse or promote products derived from this 
 *    software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package lazybones;

import java.util.logging.Level;

import javax.swing.JOptionPane;

public class Logger {
    
    public static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(LazyBones.class.getName());
    
    public final static int CONNECTION = 0;
    public final static int EPG = 1;
    public final static int OTHER = 2;
    
    public final static LoggingLevel DEBUG = new LoggingLevel("LAZYBONES DEBUG",56738);
    public final static LoggingLevel INFO = new LoggingLevel("LAZYBONES INFO",56739);
    public final static LoggingLevel WARN = new LoggingLevel("LAZYBONES WARN",56740);
    public final static LoggingLevel ERROR = new LoggingLevel("LAZYBONES ERROR",56741);
    public final static LoggingLevel FATAL = new LoggingLevel("LAZYBONES FATAL",56742);
    
    private static Logger instance;
    
    public static boolean logConnectionErrors = true;
    public static boolean logEPGErrors = true;
    
    private Logger() {
        
    }
    
    public static Logger getLogger() {
        if(instance == null) {
            instance = new Logger();
        }
        return instance;
    }
    
    public void log(Object o, int type, LoggingLevel level) {
        switch(type) {
            case CONNECTION:
                if(logConnectionErrors) {
                    LOG.log(level, o.toString());
                    if(level == ERROR || level == FATAL) {
                        JOptionPane.showMessageDialog(null, o.toString());
                    }
                }
                break;
            case EPG:
                if(logEPGErrors) {
                    LOG.log(level, o.toString());
                    if(level == ERROR || level == FATAL) {
                        JOptionPane.showMessageDialog(null, o.toString());
                    }
                }
                break;
            default:
                LOG.log(level, o.toString());
                if(level == ERROR || level == FATAL) {
                    JOptionPane.showMessageDialog(null, o.toString());
                }
                break;
        }
    }
    
    private static class LoggingLevel extends Level {
        private LoggingLevel(String name, int level) {
            super(name, level);
        }
    }
}
