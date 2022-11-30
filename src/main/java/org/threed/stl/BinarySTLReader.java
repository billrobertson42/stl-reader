package org.threed.stl;

// Copyright 2022 William Robertson
//
//    Licensed under the Apache License, Version 2.0 (the "License"); you
//    may not use this file except in compliance with the License.
//
//    You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
//    implied. See the License for the specific language governing
//    permissions and limitations under the License.

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.BiConsumer;

/**
 * A really basic stl reader that only processes well-formed
 * binary stl files.
 * <p>
 * It ignores the file header and the extra 2-byte triangle
 * record attributes.
 * <p>
 * It also does not make assumptions about what you want ot do
 * with the data, instead you pass in a BiConsumer that accepts the
 * face index and the STLFace and use that to do with the data
 * what you will.
 */
public class BinarySTLReader implements AutoCloseable {

    private static final int TRIANGLE_SIZE = 50;
    public static final int STL_HEADER_SIZE = 80;

    private final FileChannel input;
    private long expectedTriangles;

    public BinarySTLReader(Path p) throws IOException {
        input = FileChannel.open(p, StandardOpenOption.READ);
    }

    public long readTriangles(BiConsumer<Long, STLFace> faceConsumer) throws IOException {
        skipHeader();
        expectedTriangles = readTriangleCount();

        ByteBuffer buffer = ByteBuffer.allocate(TRIANGLE_SIZE * 2048);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        int bytesRead;
        long trianglesRead = 0;
        STLFace f = new STLFace();

        do {
            bytesRead = readFully(buffer);
            if (bytesRead != -1) {
                if (bytesRead % TRIANGLE_SIZE != 0) {
                    throw new EOFException("Unexpected termination of file (triangle data)");
                }

                int numTriangles = bytesRead / TRIANGLE_SIZE;
                for (int c = 0; c < numTriangles; ++c) {
                    buffer.position(c * TRIANGLE_SIZE);
                    buffer.limit(c * TRIANGLE_SIZE + TRIANGLE_SIZE);
                    faceConsumer.accept(trianglesRead++, f.withData(buffer.asFloatBuffer()));
                }
            }

        } while (bytesRead != -1);

        input.close();
        return trianglesRead;

    }

    /**
     * Return the number of triangles reported by the file. The result
     * is not valid until after calling readTriangles().
     *
     * @return the number of triangles reported by the file
     */
    public long expectedTriangles() {
        return expectedTriangles;
    }

    /**
     * Position file pointer past the 80 byte header at the beginning.
     *
     * @throws IOException per FileChannel.position()
     */
    private void skipHeader() throws IOException {
        input.position(STL_HEADER_SIZE);
    }

    /**
     * Interpret the next four bytes in the file as a UINT32.
     * This is only valid after calling skipHeader.
     *
     * @return the number of triangles in the file.
     * @throws EOFException if there was insufficient data in the file to read the 4 bytes
     * @throws IOException  per FileChannel.read()
     */
    private long readTriangleCount() throws IOException {
        ByteBuffer b = ByteBuffer.allocate(4);
        b.order(ByteOrder.LITTLE_ENDIAN);
        if (readFully(b) != 4) {
            throw new EOFException("Unexpected termination of file (triangle count)");
        }
        return Integer.toUnsignedLong(b.getInt());
    }

    /**
     * Attempt to fill the buffer from the file channel.
     * <ol>
     * <li>Clears the buffer</li>
     * <li>Attempt to fill buffer from file channel</li>
     * <li>Flip the buffer if any data has been read</li>
     * </ol>
     *
     * @param buffer the buffer to read into
     * @return the number of bytes read, -1 if at eof
     * @throws IOException per FileChannel.read()
     */
    private int readFully(ByteBuffer buffer) throws IOException {
        buffer.clear();
        int bytesToRead = buffer.capacity();
        int bytesRead = 0;
        do {
            int br = input.read(buffer);
            if (br == -1 && bytesRead == 0) {
                return -1;
            } else if (br == -1) {
                return bytesRead;
            }

            bytesRead += br;
        } while (bytesRead < bytesToRead);

        if (bytesRead != -1) {
            buffer.flip();
        }
        return bytesRead;
    }

    /**
     * Close the underlying FileChannel
     *
     * @throws IOException per FileChannel.close()
     */
    public void close() throws IOException {
        input.close();
    }
}
