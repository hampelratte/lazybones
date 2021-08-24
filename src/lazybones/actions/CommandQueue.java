/*
 * Copyright (c) Henrik Niehaus & Lazy Bones development team
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.ListModel;
import javax.swing.SwingWorker;
import javax.swing.event.ListDataListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lazybones.LazyBones;

public class CommandQueue extends ConcurrentLinkedQueue<VDRAction> implements ListModel<VDRAction>, Runnable {

    private static final long serialVersionUID = 1L;

    private static CommandQueue instance;

    private static transient Logger logger = LoggerFactory.getLogger(CommandQueue.class);

    private boolean running = false;

    private static final Cursor WAITING_CURSOR = new Cursor(Cursor.WAIT_CURSOR);
    private static final Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);

    private CommandQueue() {
        Thread t = new Thread(this);
        t.setName("Lazy Bones CommandQueue");
        t.start();
    }

    @Override
    public boolean add(VDRAction o) {
        logger.debug("Enqueued {}", o);
        return super.add(o);
    }

    public static synchronized CommandQueue getInstance() {
        if (instance == null) {
            instance = new CommandQueue();
        }
        return instance;
    }

    @Override
    public void run() {
        running = true;
        ExecutorService worker = Executors.newSingleThreadExecutor();

        while (running) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            	Thread.currentThread().interrupt();
            }

            while (size() > 0 && running) {
                final VDRAction action = poll();
                LazyBones.getInstance().getParent().setCursor(WAITING_CURSOR);
                LazyBones.getInstance().getMainDialog().setCursor(WAITING_CURSOR);

                worker.execute(new SwingWorker<Boolean, Object>() {
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
                });
            }
        }
    }

    public void stopThread() {
        this.running = false;
    }

    // ######################### stuff for ListModel #######################################
    private transient List<ListDataListener> listDataListeners = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void addListDataListener(ListDataListener l) {
        listDataListeners.add(l);
    }

    @Override
    public VDRAction getElementAt(int index) {
        int count = 0;
        for (Iterator<VDRAction> iter = iterator(); iter.hasNext(); count++) {
            VDRAction element = iter.next();
            if (count == index) {
                return element;
            }
        }

        throw new NoSuchElementException("No element with index " + index);
    }

    @Override
    public int getSize() {
        return size();
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        listDataListeners.remove(l);
    }
    // ######################### end stuff for ListModel #######################################
}