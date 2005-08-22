/* $Id: TimerProgram.java,v 1.2 2005-08-22 15:07:46 hampelratte Exp $
 * 
 * Copyrimport tvdataservice.MutableProgram;
 import de.hampelratte.svdrp.responses.highlevel.VDRTimer;
 import devplugin.Channel;
 import devplugin.Date;
 ith or without
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

import tvdataservice.MutableProgram;
import de.hampelratte.svdrp.responses.highlevel.VDRTimer;
import devplugin.Channel;
import devplugin.Date;

/**
 * A Program, which contains its' according timer
 * 
 * @author <a href="hampelratte@users.sf.net>hampelratte@users.sf.net</a>
 * 
 */
public class TimerProgram extends MutableProgram {

    private VDRTimer timer;

    public TimerProgram(Channel arg0, Date arg1) {
        super(arg0, arg1);
    }

    public TimerProgram(Channel arg0, Date arg1, int arg2, int arg3) {
        super(arg0, arg1, arg2, arg3);
    }

    public VDRTimer getTimer() {
        return timer;
    }

    public void setTimer(VDRTimer timer) {
        this.timer = timer;
    }
}
