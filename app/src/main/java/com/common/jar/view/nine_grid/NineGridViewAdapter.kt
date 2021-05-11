package com.common.jar.view.nine_grid

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import java.io.Serializable;

/**
 * @author ���ۺ�
 *
 * @features �Ź�����ͼ������
 */
abstract class NineGridViewAdapter(val context: Context, val imageInfo: MutableList<ImageInfo>) :
    Serializable {

    /**
     * ���Ҫʵ��ͼƬ������߼�����д�˷�������
     *
     * @param context      ������
     * @param nineGridView �Ź���ؼ�
     * @param index        ��ǰ���ͼƬ�ĵ�����
     * @param imageInfo    ͼƬ��ַ�����ݼ���
     */
    open fun onImageItemClick(
        context: Context,
        nineGridView: NineGridView,
        index: Int,
        imageInfo: MutableList<ImageInfo>
    ) {
    }

    /**
     * ����ImageView�����ķ�ʽ��Ĭ��ʹ��NineGridImageViewWrapper�࣬�����ͼƬ��ͼƬ�����ɰ�Ч��
     * �����Ҫ�Զ���ͼƬչʾЧ������д�˷�������
     *
     * @param context ������
     * @return ���ɵ� ImageView
     */
    open fun generateImageView(context: Context): AppCompatImageView {
        return AppCompatImageView(context)
    }
}