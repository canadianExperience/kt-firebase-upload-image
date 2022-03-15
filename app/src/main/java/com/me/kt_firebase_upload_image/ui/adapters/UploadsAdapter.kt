package com.me.kt_firebase_upload_image.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.me.kt_firebase_upload_image.databinding.UploadRowBinding
import com.me.kt_firebase_upload_image.model.UploadImage


class UploadsAdapter(
    private val listener: IRemoveClickListener
): RecyclerView.Adapter<UploadsAdapter.MyViewHolder>() {

    private var uploads = emptyList<UploadImage>()

    class MyViewHolder(private val binding: UploadRowBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(upload: UploadImage, listener: IRemoveClickListener){
            binding.upload = upload
            binding.clickListener = listener
            binding.executePendingBindings()
        }

        companion object{
            fun from(parent: ViewGroup): MyViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = UploadRowBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
       return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentUpload = uploads[position]
        holder.bind(currentUpload, listener)
    }

    override fun getItemCount(): Int {
       return uploads.size
    }

    fun setData(newData: List<UploadImage>){
        val myDiffUtil = MyDiffUtil(uploads, newData)
        val diffUtilResult = DiffUtil.calculateDiff(myDiffUtil)
        uploads = newData

        diffUtilResult.dispatchUpdatesTo(this)
    }
}