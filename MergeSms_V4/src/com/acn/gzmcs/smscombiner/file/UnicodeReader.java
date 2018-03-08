package com.acn.gzmcs.smscombiner.file;

import java.io.IOException;
import java.io.Reader;

public class UnicodeReader
{
  final Reader in;

  UnicodeReader(Reader in)
  {
    this.in = in;
  }

  public void mark(int limit)
    throws IOException
  {
    this.in.mark(limit * 2);
  }

  public void reset()
    throws IOException
  {
    this.in.reset();
  }

  public int read()
    throws IOException
  {
    int ret = this.in.read();
    if (ret == -1)
      return ret;
    if ((ret >= 55296) && (ret < 56320))
    {
      int low = this.in.read();
      if ((low >= 56320) && (low < 57344))
        ret = Character.toCodePoint((char)ret, (char)low);
      else
        throw new IOException("unpaired surrogate: U+" + 
          Integer.toHexString(ret));
    }
    else if ((ret >= 56320) && (ret < 57344)) {
      throw new IOException("unpaired surrogate: U+" + 
        Integer.toHexString(ret));
    }return ret;
  }

  public int read(int[] buf, int off, int len)
    throws IOException
  {
    if (len == 0)
      return 0;
    char[] b2 = new char[len];
    int ret = this.in.read(b2, 0, len);
    if (ret <= 0)
      return ret;
    int l = ret - 1;
    int i = 0; int j = off;
    for (; i < l; i++)
    {
      char c = b2[i];
      if ((c >= 55296) && (c < 56320))
      {
        char d = b2[(i + 1)];
        if ((d >= 56320) && (d < 57344))
        {
          buf[(j++)] = Character.toCodePoint(c, d);
          i++;
        }
        else
        {
          throw new IOException("unpaired surrogate: U+" + 
            Integer.toHexString(c));
        }
      } else {
        if ((c >= 56320) && (c < 57344))
          throw new IOException("unpaired surrogate: U+" + 
            Integer.toHexString(c));
        buf[(j++)] = c;
      }
    }
    if (i == l)
    {
      char c = b2[l];
      if ((c >= 55296) && (c < 56320))
      {
        int low = this.in.read();
        if ((low >= 56320) && (low < 57344))
        {
          buf[(j++)] = Character.toCodePoint(c, (char)low);
          return j;
        }

        throw new IOException("unpaired surrogate: U+" + 
          Integer.toHexString(c));
      }
      if ((c >= 56320) && (c < 57344))
        throw new IOException("unpaired surrogate: U+" + 
          Integer.toHexString(c));
      buf[(j++)] = c;
    }
    return j;
  }

  public void close()
    throws IOException
  {
    this.in.close();
  }

  public static int[] toCodePointArray(String text)
    throws IOException
  {
    char[] b2 = text.toCharArray();
    int[] buf = new int[b2.length];
    if (b2.length > 0)
    {
      int l = b2.length - 1;
      int i = 0; int j = 0;
      for (; i < l; i++)
      {
        char c = b2[i];
        if ((c >= 55296) && (c < 56320))
        {
          char d = b2[(i + 1)];
          if ((d >= 56320) && (d < 57344))
          {
            buf[(j++)] = Character.toCodePoint(c, d);
            i++;
          }
          else
          {
            throw new IOException("unpaired surrogate: U+" + 
              Integer.toHexString(c));
          }
        } else {
          if ((c >= 56320) && (c < 57344))
            throw new IOException("unpaired surrogate: U+" + 
              Integer.toHexString(c));
          buf[(j++)] = c;
        }
      }
      if (i == l)
      {
        buf[(j++)] = b2[l];
        if (j < buf.length)
        {
          int[] buf2 = new int[j];
          System.arraycopy(buf, 0, buf2, 0, j);
          buf = buf2;
        }
      }
    }
    return buf;
  }
}
