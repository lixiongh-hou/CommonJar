package com.common.jar.view.nine_grid

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import com.common.jar.R
import com.common.tool.util.dpToPx


/**
 * ���ģʽ��������΢��
 */
const val MODE_FILL = 0

/**
 * ����ģʽ��������QQ��4��ͼ�� 2X2����
 */
const val MODE_GRID = 1


/**
 * @author ���ۺ�
 *
 * @features �Ź���ͼ
 */
class NineGridView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {
    /**
     * ����ͼƬʱ������С,��λdp
     */
    private var singleImageSize = 250.dpToPx

    /**
     * ����ͼƬ�Ŀ�߱�(��/��)
     */
    private var singleImageRatio = 1.0F

    /**
     * �����ʾ��ͼƬ��
     */
    private var maxImageSize = 9

    /**
     * �����࣬��λdp
     */
    private var gridSpacing = 3.dpToPx

    /**
     * Ĭ��ʹ��fillģʽ
     */
    private var mode = MODE_FILL

    /**
     * ����
     */
    private var columnCount = 0

    /**
     * ����
     */
    private var rowCount = 0

    /**
     * ������
     */
    private var gridWidth = 0

    /**
     * ����߶�
     */
    private var gridHeight = 0

    private var imageViews = mutableListOf<AppCompatImageView>()
    private var mImageInfo: MutableList<ImageInfo>? = null
    private lateinit var mAdapter: NineGridViewAdapter

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.NineGridView).apply {
            gridSpacing =
                getDimension(
                    R.styleable.NineGridView_ngv_gridSpacing,
                    gridSpacing.toFloat()
                ).toInt()
            singleImageSize = getDimensionPixelSize(
                R.styleable.NineGridView_ngv_singleImageSize,
                singleImageSize
            )
            singleImageRatio =
                getFloat(R.styleable.NineGridView_ngv_singleImageRatio, singleImageRatio)
            maxImageSize = getInt(R.styleable.NineGridView_ngv_maxSize, maxImageSize)
            mode = getInt(R.styleable.NineGridView_ngv_mode, mode)
        }
        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var width = MeasureSpec.getSize(widthMeasureSpec)
        var height = 0
        val totalWidth = width - paddingLeft - paddingRight
        mImageInfo?.let {
            if (it.isNotEmpty()) {
                if (it.size == 1) {
                    gridWidth = if (singleImageSize > totalWidth) totalWidth else singleImageSize
                    gridHeight = (gridWidth / singleImageRatio).toInt()
                    //����ͼƬ��ʾ�����С���������������ʾ��Χ
                    if (gridHeight > singleImageSize) {
                        val ratio = singleImageSize * 1.0f / gridHeight
                        gridWidth = (gridWidth * ratio).toInt()
                        gridHeight = singleImageSize
                    }
                } else {
                    //���������Ǽ���ͼƬ����߶����ܿ�ȵ� 1/3
                    gridWidth = (totalWidth - gridSpacing * 2) / 3
                    gridHeight = (totalWidth - gridSpacing * 2) / 3
                }
                width =
                    gridWidth * columnCount + gridSpacing * (columnCount - 1) + paddingLeft + paddingRight
                height =
                    gridHeight * rowCount + gridSpacing * (rowCount - 1) + paddingTop + paddingBottom
            }
        }

        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        mImageInfo?.let {
            it.forEachIndexed { index, _ ->
                val childrenView = getChildAt(index) as AppCompatImageView
                val rowNum = index / columnCount
                val columnNum = index % columnCount
                val left = (gridWidth + gridSpacing) * columnNum + paddingLeft
                val top = (gridHeight + gridSpacing) * rowNum + paddingTop
                val right = left + gridWidth
                val bottom = top + gridHeight
                childrenView.layout(left, top, right, bottom)
                mImageLoader?.onDisplayImage(context, childrenView, it[index].thumbnailUrl)
            }
        }


    }

    fun setAdapter(adapter: NineGridViewAdapter) {
        mAdapter = adapter
        var imageInfo: MutableList<ImageInfo> = adapter.imageInfo
        if (imageInfo.isNullOrEmpty()) {
            visibility = GONE
            return
        } else {
            visibility = VISIBLE
        }
        var imageCount = imageInfo.size
        if (maxImageSize in 1 until imageCount) {
            imageInfo = imageInfo.subList(0, maxImageSize)
            //�ٴλ�ȡͼƬ����
            imageCount = imageInfo.size
        }
        //Ĭ����3����ʾ����������ͼƬ����������
        rowCount = imageCount / 3 + if (imageCount % 3 == 0) 0 else 1
        columnCount = 3
        //gridģʽ�£���ʾ4��ʹ��2X2ģʽ
        if (mode == MODE_GRID) {
            if (imageCount == 4) {
                rowCount = 2
                columnCount = 2
            }
        }
        //��֤View�ĸ��ã������ظ�����
        if (mImageInfo == null) {
            for (i in 0 until imageCount) {
                val iv: AppCompatImageView = getImageView(i) ?: return
                addView(iv, generateDefaultLayoutParams())
            }
        } else {
            val oldViewCount = mImageInfo!!.size
            val newViewCount = imageCount
            if (oldViewCount > newViewCount) {
                removeViews(newViewCount, oldViewCount - newViewCount)
            } else if (oldViewCount < newViewCount) {
                for (i in oldViewCount until newViewCount) {
                    val iv: ImageView = getImageView(i) ?: return
                    addView(iv, generateDefaultLayoutParams())
                }
            }
        }
        //�޸����һ����Ŀ�������Ƿ���ʾ����
        if (adapter.imageInfo.size > maxImageSize) {
            val child = getChildAt(maxImageSize - 1)
//            if (child is NineGridViewWrapper) {
//                child.moreNum = adapter.imageInfo.size - maxImageSize
//            }
        }
        mImageInfo = imageInfo
        requestLayout()
    }

    private fun getImageView(position: Int): AppCompatImageView {
        val imageView: AppCompatImageView
        if (position < imageViews.size) {
            imageView = imageViews[position]
        } else {
            imageView = mAdapter.generateImageView(context)
            imageView.setOnClickListener {
                mAdapter.onImageItemClick(
                    context, this@NineGridView, position, mAdapter.imageInfo
                )
            }
            imageViews.add(imageView)
        }
        return imageView
    }

    /** ���ù�����  */
    fun setGridSpacing(spacing: Int) {
        gridSpacing = spacing
    }

    /** ����ֻ��һ��ͼƬʱ�Ŀ�  */
    fun setSingleImageSize(maxImageSize: Int) {
        singleImageSize = maxImageSize
    }

    /** ����ֻ��һ��ͼƬʱ�Ŀ�߱�  */
    fun setSingleImageRatio(ratio: Float) {
        singleImageRatio = ratio
    }

    /** �������ͼƬ��  */
    fun setMaxSize(maxSize: Int) {
        maxImageSize = maxSize
    }

    fun getMaxSize(): Int {
        return maxImageSize
    }

    companion object {
        /**
         * ȫ�ֵ�ͼƬ������(��������,���߲���ʾͼƬ)
         */
        private var mImageLoader: ImageLoader? = null
        fun setImageLoader(imageLoader: ImageLoader) {
            mImageLoader = imageLoader
        }

        fun getImageLoader(): ImageLoader? {
            return mImageLoader
        }
    }

    interface ImageLoader {
        /**
         * ��Ҫ����ʵ�ָ÷�������ȷ����μ��غ���ʾͼƬ
         *
         * @param context   ������
         * @param imageView ��ҪչʾͼƬ��ImageView
         * @param url       ͼƬ��ַ
         */
        fun onDisplayImage(context: Context, imageView: AppCompatImageView, url: String)

        /**
         * @param url ͼƬ�ĵ�ַ
         * @return ��ǰ��ܵı��ػ���ͼƬ
         */
        fun getCacheImage(url: String): Bitmap?
    }
}