package com.example.beerbicep.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKey(
    @PrimaryKey
    val beerId: Int,
    val prevKey: Int?,//prev page key , null bcoz first page doesn't has previous page
    val nextKey: Int?//next page key , null bcoz last page doesn't has next page
)
/*
The RemoteKey entity is used by the RemoteMediator from the Paging 3 library to manage the loading of data from the network. Here's how it works:

Loading Data: When the RemoteMediator needs to fetch a new page of data from the Punk API, it first checks the remote_keys table.

Finding the Next Page: It uses the getRemoteKeyByBeerId() method in the RemoteKeyDao to find the RemoteKey for the last beer in the currently loaded list. The nextKey property of this RemoteKey tells the RemoteMediator which page to request from the API.

Storing New Keys: After fetching a new page of beers, the RemoteMediator creates new RemoteKey objects for each new beer and inserts them into the remote_keys table using the insertAll() method in the RemoteKeyDao.

This system allows the RemoteMediator to efficiently and accurately load pages of data from the network, ensuring a smooth and seamless scrolling experience for the user.
 */