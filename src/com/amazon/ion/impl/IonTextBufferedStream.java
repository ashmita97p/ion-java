/*
 * Copyright (c) 2008 Amazon.com, Inc.  All rights reserved.
 */

package com.amazon.ion.impl;

import java.io.IOException;
import java.io.InputStream;

/**
 * InputStream implementations over a number of types (byte[] and String)
 * that do not handle character decoding.  Each byte is treated as just
 * a character from 0 to 255.  These are used internally to wire up some
 * of the Iterator code to some of the base64 encoding (or decoding)
 * routins in ion.impl. 
 */
public abstract class IonTextBufferedStream extends InputStream
{
    public static IonTextBufferedStream makeStream(byte[] bytes) {
        return new SimpleBufferStream(bytes);
    }
    public static IonTextBufferedStream makeStream(byte[] bytes, int offset, int len) {
        return new OffsetBufferStream(bytes, offset, len);
    }
    public static IonTextBufferedStream makeStream(String text) {
        return new StringStream(text);
    }
    public abstract int getByte(int pos);
    public abstract int position();
    public abstract IonTextBufferedStream setPosition(int pos);
    
    static final class SimpleBufferStream extends IonTextBufferedStream
    {
        byte [] _buffer;
        int     _len;
        int     _pos;
        
        public SimpleBufferStream(byte[] buffer) 
        {
            _buffer = buffer;
            _pos = 0;
            _len = buffer.length;
        }
        
        @Override
        public final int getByte(int pos) {
            if (pos < 0 || pos >= _len) return -1;
            return _buffer[pos] & 0xff;
        }
    
        @Override
        public final int read()
            throws IOException
        {
            if (_pos >= _len) return -1;
            return _buffer[_pos++];
        }
        
        @Override
        public final int read(byte[] bytes, int offset, int len) throws IOException
        {
            int copied = 0;
            if (offset < 0) throw new IllegalArgumentException();
            copied = len;
            if (_pos + len >= _len) copied = _len - _pos;
            System.arraycopy(_buffer, _pos, bytes, offset, copied);
            _pos += copied;
            return copied;
        }

        @Override
        public final int position() {
            return _pos;
        }
       
        @Override
        public final SimpleBufferStream setPosition(int pos)
        {
            if (_pos < 0 || _pos > _len) throw new IllegalArgumentException();
            _pos = pos;
            return this;
        }
    }
    
    static final class StringStream extends IonTextBufferedStream
    {
        
        String _string;
        int    _end;
        int    _pos;
        
        public StringStream(String text) 
        {
            _string = text;
            _pos = 0;
            _end = text.length();
        }
        
        @Override
        public final int getByte(int pos) {
            if (pos < 0) return -1;
            if (pos >= _end) return -1;
            return _string.charAt(pos); 
        }
    
        @Override
        public final int read()
            throws IOException
        {
            if (_pos >= _end) return -1;
            char c = _string.charAt(_pos++); 
            return c;
        }
        
        @Override
        public final int read(byte[] bytes, int offset, int len) throws IOException
        {
            throw new UnsupportedOperationException();
        }
    
        @Override
        public final int position() {
            return _pos;
        }
       
        @Override
        public final StringStream setPosition(int pos)
        {
            if (pos < 0) throw new IllegalArgumentException();
            if (pos > _end) throw new IllegalArgumentException();
            _pos = pos;
            return this;
        }
    }
    
    static final class OffsetBufferStream extends IonTextBufferedStream
    {
        byte [] _buffer;
        int     _start;
        int     _end;
        int     _pos;
        
        public OffsetBufferStream(byte[] buffer, int start, int max) 
        {
            _buffer = buffer;
            _pos = start;
            _start = start;
            _end = start + max;
        }

        @Override
        public final int getByte(int pos) {
            if (pos < 0) return -1;
            pos += _start;
            if (pos >= _end) return -1;
            return _buffer[pos] & 0xff;
        }
    
        @Override
        public final int read()
            throws IOException
        {
        	int c; 
    		if (_pos >= _end) return -1;
    		c = (((int)_buffer[_pos++]) & 0xFF);  // trim sign extension bits
            return c; 
        }

        @Override
        public final int read(byte[] bytes, int offset, int len) throws IOException
        {
            int copied = 0;
            if (offset < 0) throw new IllegalArgumentException();
            copied = len;
            if (_pos + len >= _end) copied = _end - _pos;
            System.arraycopy(_buffer, _pos, bytes, offset, copied);
            _pos += copied;
            return copied;
        }

        @Override
        public final int position() {
            return _pos - _start;
        }

        @Override
        public final OffsetBufferStream setPosition(int pos)
        {
            if (pos < 0) throw new IllegalArgumentException();
            pos += _start;
            if (pos > _end) throw new IllegalArgumentException();
            _pos = pos;
            return this;
        }
    }
}
