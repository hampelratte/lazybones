/* $Id: CommandQueue.java,v 1.4 2010-09-28 21:30:11 hampelratte Exp $
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
package lazybones.actions;

import java.awt.Cursor;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;

import javax.swing.ListModel;
import javax.swing.SwingWorker;
import javax.swing.event.ListDataListener;

import lazybones.LazyBones;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CommandQueue extends ConcurrentLinkedQueue<VDRAction> implements ListModel, Runnable {

    private static CommandQueue instance;
    
    private static transient Logger logger = LoggerFactory.getLogger(CommandQueue.class);
    
    private boolean running = false;
    
    private final Cursor WAITING_CURSOR = new Cursor(Cursor.WAIT_CURSOR);
    private final Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);

    private CommandQueue() {
        Thread t = new Thread(this);
        t.setName("Lazy Bones CommandQueue");
        t.start();
    }

    @Override
    public boolean add(VDRAction o) {
        logger.debug("Enqueued {}", o.toString());
        return super.add(o);
    }

    public synchronized static CommandQueue getInstance() {
        if (instance == null) {
            instance = new CommandQueue();
        }
        return instance;
    }
    
    public void run() {
        running = true;
        while(true) {
            try { Thread.sleep(10); } catch (InterruptedException e) {}
            
            while(size() > 0 && running) {
                final VDRAction action = poll();
                LazyBones.getInstance().getParent().setCursor(WAITING_CURSOR);
                LazyBones.getInstance().getMainDialog().setCursor(WAITING_CURSOR);

                Executors.newSingleThreadExecutor().execute(
                    new SwingWorker<Boolean, Object>() {
                        @Override
                        protected Boolean doInBackground() throws Exception {
                            boolean success = action.execute();
                            action.setSuccess(success);
                            action.callback();
                            return success;
                        }
                        
                        @Override
                        protected void done() {
                            LazyBones.getInstance().getParent().setCursor(DEFAULT_CURSOR);
                            LazyBones.getInstance().getMainDialog().setCursor(DEFAULT_CURSOR);
                        }
                    }
                );
            }
        }
    }
    
    public void stopThread() {
        this.running = false;
    }
    
    
//######################### stuff for ListModel #######################################    
    private Vector<ListDataListener> listDataListeners = new Vector<ListDataListener>();
    
    public void addListDataListener(ListDataListener l) {
        listDataListeners.add(l);
    }

    public Object getElementAt(int index) {
        int count = 0;
        for (Iterator<VDRAction> iter = iterator(); iter.hasNext(); count++) {
            Object element = iter.next();
            if(count == index) {
                return element;
            }
        }
        
        throw new NoSuchElementException("No element with index " + index);
    }

    public int getSize() {
        return size();
    }

    public void removeListDataListener(ListDataListener l) {
        listDataListeners.remove(l);
    }
//  ######################### end stuff for ListModel ####################################### 
}