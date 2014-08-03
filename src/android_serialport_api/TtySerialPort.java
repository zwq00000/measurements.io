/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package android_serialport_api;

import android.util.Log;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class TtySerialPort implements SerialPort {

	private static final String TAG = "SerialPort";

	/*
	 * Do not remove or rename the field mFd: it is used by native method close();
	 */
	private final FileDescriptor mFd;
	private final FileInputStream mInput;
	private final FileOutputStream mOutput;
    private final String portName;

	public TtySerialPort(@NotNull File device, int baudRate, int flags) throws SecurityException, IOException {

		/* Check access permission */
		if (!device.canRead() || !device.canWrite()) {
			/*try {
				// Missing read/write permission, trying to chmod the file
				Process su;
				su = Runtime.getRuntime().exec("/system/bin/su");
				String cmd = "chmod 777 " + device.getAbsolutePath() + "\n" + "exit\n";
                OutputStream outputStream = su.getOutputStream();
                outputStream.write(cmd.getBytes());
                outputStream.flush();
                outputStream.close();
				if ((su.waitFor() != 0) || !device.canRead()
						|| !device.canWrite()) {
					throw new SecurityException("chmod "+device.getAbsolutePath() + " timeout or can't write");
				}
			} catch (Exception e) {
				Log.w(TAG,e.getMessage());
				//throw new SecurityException(e.getMessage());
			}*/
		}

            String devicePath = device.getAbsolutePath();
            FileUtilsProxy.setPermissions(device,777,-1,-1);
		mFd = open(devicePath, baudRate, flags);
		if (mFd == null) {
			Log.e(TAG, "native open returns null");
			throw new IOException("native open file "+ device.getAbsolutePath() + " fail");
		}
		mInput = new FileInputStream(mFd);
		mOutput = new FileOutputStream(mFd);
        portName = device.getAbsolutePath();
	}

    public String getPortName(){
        return portName;
    }

	// Getters and setters
	public InputStream getInputStream() {
		return mInput;
	}

	public OutputStream getOutputStream() {
		return mOutput;
	}

    /**
     * Reads as many bytes as possible into the destination buffer.
     *
     * @param buffer the destination byte buffer
     * @return the actual number of bytes read
     * @throws java.io.IOException if an error occurred during reading
     */
    @Override
    public int read(byte[] buffer) throws IOException {
        return this.mInput.read(buffer);
    }

    /**
     * Equivalent to {@code write(buffer, 0, buffer.length)}.
     *
     * @param buffer
     */
    @Override
    public void write(byte[] buffer) throws IOException {
        this.mOutput.write(buffer);
    }

    /**
     * Flushes this stream. Implementations of this method should ensure that
     * any buffered mFrame is written out. This implementation does nothing.
     *
     * @throws java.io.IOException if an error occurs while flushing this stream.
     */
    @Override
    public void flush() throws IOException {
        this.mOutput.flush();
    }

    // JNI
	@NotNull
    private native static FileDescriptor open(String path, int baudRate, int flags);
	public native void close();
	static {
		System.loadLibrary("serial_port");
	}
}
