/* $Id: VerticalFlowLayout.java,v 1.1 2006-07-21 11:59:37 hampelratte Exp $
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
package lazybones.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

public class VerticalFlowLayout implements LayoutManager {

    private int vgap = 0;
    private int hgap = 0;

    public VerticalFlowLayout() {
        this(0);
    }

    public VerticalFlowLayout(int vgap) {
       this(vgap, 0);
    }
    
    public VerticalFlowLayout(int vgap, int hgap) {
    	this.vgap = vgap > 0 ? vgap : 0;
    	this.hgap = hgap > 0 ? hgap : 0;
    }

    public void addLayoutComponent(String name, Component comp) {
    }

    public void layoutContainer(Container parent) {
        Insets insets = parent.getInsets();
        int w = parent.getSize().width - insets.left - insets.right - vgap * 2;

        // int h = parent.size().height - insets.top - insets.bottom;
        int numComponents = parent.getComponentCount();

        if (numComponents == 0) {
            return;
        }
        int y = insets.top + vgap;
        int x = insets.left + hgap;

        for (int i = 0; i < numComponents; ++i) {
            Component c = parent.getComponent(i);

            if (c.isVisible()) {
                Dimension d = c.getPreferredSize();

                c.setBounds(x, y, w, d.height);
                y += d.height + vgap;
            }
        }
    }

    public Dimension minimumLayoutSize(Container parent) {
        Insets insets = parent.getInsets();
        int maxWidth = 0;
        int totalHeight = 0;
        int numComponents = parent.getComponentCount();

        for (int i = 0; i < numComponents; ++i) {
            Component c = parent.getComponent(i);

            if (c.isVisible()) {
                Dimension cd = c.getMinimumSize();

                maxWidth = Math.max(maxWidth, cd.width);
                totalHeight += cd.height;
            }
        }
        Dimension td = new Dimension(maxWidth + insets.left + insets.right + hgap * 2,
                totalHeight + insets.top + insets.bottom + vgap * numComponents);

        return td;
    }

    public Dimension preferredLayoutSize(Container parent) {
        Insets insets = parent.getInsets();
        int maxWidth = 0;
        int totalHeight = 0;
        int numComponents = parent.getComponentCount();

        for (int i = 0; i < numComponents; ++i) {
            Component c = parent.getComponent(i);

            if (c.isVisible()) {
                Dimension cd = c.getPreferredSize();

                maxWidth = Math.max(maxWidth, cd.width);
                totalHeight += cd.height;
            }
        }
        Dimension td = new Dimension(maxWidth + insets.left + insets.right + hgap * 2,
                totalHeight + insets.top + insets.bottom + vgap * numComponents);

        return td;
    }


    public void removeLayoutComponent(Component comp) {
    }
}