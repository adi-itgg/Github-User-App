package me.phantomx.githubuserapp.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import me.phantomx.githubuserapp.R
import me.phantomx.githubuserapp.activity.DetailsActivity
import me.phantomx.githubuserapp.data.entity.User
import me.phantomx.githubuserapp.databinding.ItemAdapterBinding

class UserAdapter(
    private val activity: Activity,
    private var data: List<User> = listOf()
): RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemAdapterBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = with(holder.binding) {
        data[position].let {
            with(root.context) {
                image.load(it.avatarUrl) {
                    placeholder(R.drawable.ic_account_circle)
                }

                name.text = it.name
                username.text = it.login

                root.setOnClickListener { _ ->
                    startActivity(
                        Intent(this, DetailsActivity::class.java)
                            .putExtra(User::class.simpleName, it),
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            activity,
                            Pair(image, resources.getString(R.string.thumbnail)),
                            Pair(name, resources.getString(R.string.name))
                        ).toBundle()
                    )
                }

            }
        }
    }

    override fun getItemCount() = data.size

    fun update(newData: List<User>) {
        DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = data.size
            override fun getNewListSize() = newData.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                data[oldItemPosition].id == newData[newItemPosition].id

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                data[oldItemPosition].avatarUrl == newData[newItemPosition].avatarUrl

        }).dispatchUpdatesTo(this)
        data = newData
    }

}