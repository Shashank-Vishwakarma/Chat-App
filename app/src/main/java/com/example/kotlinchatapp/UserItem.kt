package com.example.kotlinchatapp

import android.widget.TextView
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView

class UserItem(val user: User?) : Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.user_item_list
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {

        val usernametextview: TextView = viewHolder.itemView.findViewById(R.id.username_recyclerview)
        val profileImage: CircleImageView = viewHolder.itemView.findViewById(R.id.cirecleimageview_recyclerview)

        usernametextview.text = user!!.username
        Picasso.get().load(user.profileUrl).placeholder(R.drawable.ic_launcher_background).into(profileImage)
    }
}