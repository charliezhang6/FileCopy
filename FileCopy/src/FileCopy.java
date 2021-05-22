import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileCopy {
    public static void main(String[] args) {
        FileCopyRunner noBufferStreamCopy = new FileCopyRunner() {
            @Override
            public void copyFile(File source, File target) {
                InputStream fin = null;
                OutputStream fout =null;
                try {
                    fin =new FileInputStream(source);
                    fout=new FileOutputStream(target);
                    int result;
                    while((result=fin.read())!=-1){
                        fout.write(result);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    close(fin);
                    close(fout);
                }
            }
        };

        FileCopyRunner BufferedStreamCopy = new FileCopyRunner() {
            @Override
            public void copyFile(File source, File target) {
                InputStream fin=null;
                OutputStream fout=null;
                try {
                    fin=new FileInputStream(source);
                    fout=new FileOutputStream(target);
                    byte[] buffer = new byte[1024];
                    int result;
                    while((result=fin.read(buffer))!=-1){
                        fout.write(buffer,0,result);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    close(fin);
                    close(fout);
                }
            }
        };

        FileCopyRunner nioBufferCopy = new FileCopyRunner() {
            @Override
            public void copyFile(File source, File target) {
                FileChannel fin=null;
                FileChannel fout=null;
                try {
                    fin =new FileInputStream(source).getChannel();
                    fout=new FileOutputStream(target).getChannel();
                    ByteBuffer buffer=ByteBuffer.allocate(1024);
                    while((fin.read(buffer))!=-1){
                        buffer.flip();
                        while (buffer.hasRemaining()) {
                            fout.write(buffer);
                        }
                        buffer.clear();
                    }
                    buffer.clear();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    close(fin);
                    close(fout);
                }
            }
        };

        FileCopyRunner nioTransferCopy = new FileCopyRunner() {
            @Override
            public void copyFile(File source, File target) {
                FileChannel fin=null;
                FileChannel fout=null;
                try {
                    fin=new FileInputStream(source).getChannel();
                    fout=new FileOutputStream(target).getChannel();
                    long size=fin.size();
                    long transferred =0L;
                    while (transferred<size) {
                        transferred+=fin.transferTo(0,size,fout);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    close(fin);
                    close(fout);
                }
            }
        };

        File source=new File("/Users/charliezhang/Desktop/test1.txt");
        File target=new File("/Users/charliezhang/Desktop/test2.md");
        noBufferStreamCopy.copyFile(source,target);
        BufferedStreamCopy.copyFile(source,target);
        nioBufferCopy.copyFile(source,target);
        nioTransferCopy.copyFile(source,target);
    }


    private static void close(Closeable closeable) {
        if(closeable!=null){
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
interface FileCopyRunner {
    void copyFile(File source,File target);
}
