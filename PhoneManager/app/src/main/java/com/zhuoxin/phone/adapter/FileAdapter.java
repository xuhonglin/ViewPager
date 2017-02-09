package com.zhuoxin.phone.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhuoxin.phone.R;
import com.zhuoxin.phone.base.MyBaseAdapter;
import com.zhuoxin.phone.utils.FileTypeUtil;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;

import entity.FileInfo;

/**
 * Created by Administrator on 2016/11/24.
 */

public class FileAdapter extends MyBaseAdapter<FileInfo> {
    public boolean isScroll = false;
    //1、创建软引用的键值对
    //HashMap<String, SoftReference<Bitmap>> bitmapSoftMap = new HashMap<String, SoftReference<Bitmap>>();
    int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
    private final int LRUSIZE = maxMemory / 16;//KB   10*1024*1024
    //1、创建LruCache，并指定临界清理值
    LruCache<String, Bitmap> bitmapLruCache = new LruCache<String, Bitmap>(LRUSIZE) {
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getHeight() * value.getRowBytes();
        }
    };

    public FileAdapter(List dataList, Context context) {
        super(dataList, context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_file, null);
            holder = new ViewHolder();
            holder.cb_file = (CheckBox) convertView.findViewById(R.id.cb_file);
            holder.iv_fileicon = (ImageView) convertView.findViewById(R.id.iv_fileicon);
            holder.tv_filename = (TextView) convertView.findViewById(R.id.tv_filename);
            holder.tv_filetype = (TextView) convertView.findViewById(R.id.tv_filetype);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //设置布局中的数据
        holder.cb_file.setTag(position);
        holder.cb_file.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //取出当前cb的位置
                int index = (int) holder.cb_file.getTag();
                //修改对应位置的数据
                getItem(index).setSelect(isChecked);
            }
        });
        holder.cb_file.setChecked(getItem(position).isSelect());
        //设置图片,传统方式R.drawable.icon_image不可拆分和判断，建议用位图来操作
        Bitmap bitmap = null;
        /*SoftReference<Bitmap> bitmapSoftReference = bitmapSoftMap.get(getItem(position).getFile().getName());
        //如果软引用中没有任何数据
        if (bitmapSoftReference == null) {
            //从系统中重新加载图片
            bitmap = getBitmap(getItem(position));
            //把加载后的图片重新设置到软引用中
            SoftReference<Bitmap> soft = new SoftReference<Bitmap>(bitmap);
            //用键值对关联软引用
            bitmapSoftMap.put(getItem(position).getFile().getName(), soft);
        } else {
            //如果软引用不为空，则直接取出图片，不用从系统中加载了
            bitmap = bitmapSoftReference.get();
        }
        */
        if (!isScroll) {
            bitmap = bitmapLruCache.get(getItem(position).getFile().getName());
            if (bitmap == null) {
                //从系统中重新加载图片
                bitmap = getBitmap(getItem(position));
                //放入lru
                bitmapLruCache.put(getItem(position).getFile().getName(), bitmap);
            }
            holder.iv_fileicon.setImageBitmap(bitmap);//添加获取位图的方法，用来根据不同的文件获取不同的位图
        } else {
            holder.iv_fileicon.setImageResource(R.drawable.item_arrow_right);
        }
        //获取文件名称
        holder.tv_filename.setText(getItem(position).getFile().getName());
        //获取文件类型
        holder.tv_filetype.setText(getItem(position).getFileType());
        return convertView;
    }

    static class ViewHolder {
        CheckBox cb_file;
        ImageView iv_fileicon;
        TextView tv_filename;
        TextView tv_filetype;
    }

    private Bitmap getBitmap(FileInfo fileInfo) {
        //定义位图
        Bitmap bitmap = null;
        //判断类型。image和非image
        //在decode图片资源之前，先获取和设置图片的缩放率
        //1、new一个options
        BitmapFactory.Options options = new BitmapFactory.Options();
        if (fileInfo.getFileType().equals(FileTypeUtil.TYPE_IMAGE)) {
            //2、把图片参数取出来，放到options中
            options.inJustDecodeBounds = true;//只解码边界
            BitmapFactory.decodeFile(fileInfo.getFile().getAbsolutePath(), options);
            //3、计算缩放率并设置
            int scaleUnit = 60;
            int scale = (options.outHeight > options.outWidth ? options.outHeight : options.outWidth) / scaleUnit;
            options.inSampleSize = scale;
            //4、根据设置好的options进行图片加载
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(fileInfo.getFile().getAbsolutePath(), options);
            return bitmap;
        } else {
            //其他情况，去drawable目录就可以了
            //通过getIdentifier方法来把fileType转换成资源文件下对应的R.id的形式
            int icon = context.getResources().getIdentifier(fileInfo.getIconName(), "drawable", context.getPackageName());
            //如果icon<=0，说明数据没有取出来，给一个默认的值
            if (icon <= 0) {
                icon = R.drawable.item_arrow_right;
            }
            //把R.id的资源转换成bitmap
            //2、把图片参数取出来，放到options中
            options.inJustDecodeBounds = true;//只解码边界
            BitmapFactory.decodeResource(context.getResources(), icon, options);
            //3、计算缩放率并设置
            int scaleUnit = 60;
            int scale = (options.outHeight > options.outWidth ? options.outHeight : options.outWidth) / scaleUnit;
            options.inSampleSize = scale;
            //4、根据设置好的options进行图片加载
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeResource(context.getResources(), icon, options);
            return bitmap;
        }

    }

    private Cursor getCursorResult(String path) {
        //通过内容提供者查询结果
        Cursor cursorResult = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        //取出结果，把结果对应的cursor返回
        while (cursorResult.moveToNext()) {
            String cursorPath = cursorResult.getString(cursorResult.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
            //如果路径和我们传进来的路径相等，直接打断
            if (cursorPath.equals(path)) {
                break;
            }

        }
        return cursorResult;
    }

    private String getAlbumArt(Cursor cursorResult) {
        int album_id = cursorResult.getInt(cursorResult.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cur = context.getContentResolver().query(
                Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)),
                projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        cur = null;
        return album_art;
    }

    private Bitmap getMP3Bitmap(String album_art) {
        Bitmap bm = null;
        if (album_art == null) {
            bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_audio);
        } else {
            bm = BitmapFactory.decodeFile(album_art);
        }
        return bm;
    }
}
