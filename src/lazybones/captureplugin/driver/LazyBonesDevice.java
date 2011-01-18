/* $Id: LazyBonesDevice.java,v 1.4 2011-01-18 13:13:57 hampelratte Exp $
 * 
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
package lazybones.captureplugin.driver;

import java.awt.Window;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import tvbrowser.core.plugin.PluginManagerImpl;
import tvdataservice.MutableProgram;
import captureplugin.drivers.DeviceIf;
import captureplugin.drivers.DriverIf;
import devplugin.PluginAccess;
import devplugin.PluginManager;
import devplugin.Program;
import devplugin.ProgramReceiveTarget;

public class LazyBonesDevice implements DeviceIf {
    
    public static final String TARGET_CAPTURE_PLUGIN_ADD = "capture_plugin_add";
    public static final String TARGET_CAPTURE_PLUGIN_REMOVE = "capture_plugin_remove";

    private List<Program> programs = new ArrayList<Program>();
    
    private String name;
    
    private DriverIf driver;
    
    private PluginAccess lazyBones;
    private ProgramReceiveTarget addTarget;
    private ProgramReceiveTarget removeTarget;
    
    public LazyBonesDevice(String name, DriverIf driver) {
        this.name = name;
        this.driver = driver;
        
        PluginManager pm = PluginManagerImpl.getInstance();
        lazyBones = pm.getActivatedPluginForId("java.lazybones.LazyBones");
        
        addTarget = new ProgramReceiveTarget(lazyBones, "Lazy Bones", TARGET_CAPTURE_PLUGIN_ADD);
        removeTarget = new ProgramReceiveTarget(lazyBones, "Lazy Bones", TARGET_CAPTURE_PLUGIN_REMOVE);
    }
    
    public boolean add(Window parent, Program program) {
        lazyBones.receivePrograms(new Program[] {program}, addTarget);
        return programs.add(program);
    }

    public Program[] checkProgramsAfterDataUpdateAndGetDeleted() {
        // TODO Auto-generated method stub
        return null;
    }

    public void configDevice(Window parent) {
        // TODO Auto-generated method stub
        
    }

    public boolean executeAdditionalCommand(Window parent, int num, Program program) {
        // TODO Auto-generated method stub
        return false;
    }

    public String[] getAdditionalCommands() {
        return new String[0];
    }

    public boolean getDeleteRemovedProgramsAutomatically() {
        // TODO Auto-generated method stub
        return false;
    }

    public DriverIf getDriver() {
        return driver;
    }

    public String getId() {
        return "lazybones." + getName();
    }

    public String getName() {
        return this.name;
    }

    public Program[] getProgramList() {
        Program[] list = new MutableProgram[0];
        return programs.toArray(list);
    }

    public boolean isAbleToAddAndRemovePrograms() {
        return true;
    }

    public boolean isInList(Program program) {
        return programs.contains(program);
    }

    public void readData(ObjectInputStream stream, boolean importDevice) throws IOException, ClassNotFoundException {
        // TODO Auto-generated method stub
        
    }

    public boolean remove(Window parent, Program program) {
        lazyBones.receivePrograms(new Program[] {program}, removeTarget);
        return programs.remove(program);
    }

    public void removeProgramWithoutExecution(Program p) {
        programs.remove(p);
    }

    public String setName(String name) {
        this.name = name;
        return name;
    }

    public void writeData(ObjectOutputStream stream) throws IOException {
        // TODO Auto-generated method stub
        
    }
    
    // TODO implement
    public Object clone() {
        return null;
    }

    @Override
    public Program getProgramForProgramInList(Program p) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void sendProgramsToReceiveTargets(Program[] progs) {
        // TODO Auto-generated method stub
        
    }
}
