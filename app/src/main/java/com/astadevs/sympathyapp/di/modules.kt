package com.astadevs.sympathyapp.di

import com.astadevs.sympathyapp.repositories.AuthRepository
import com.astadevs.sympathyapp.ui.campaigns.CampaignsViewModel
import com.astadevs.sympathyapp.ui.maps.MapsViewModel
import com.astadevs.sympathyapp.ui.login.LoginViewModel
import com.astadevs.sympathyapp.ui.organization.OrganizationViewModel
import com.astadevs.sympathyapp.ui.profile.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Firebase
    single { FirebaseAuth.getInstance() }
    single { FirebaseDatabase.getInstance() }
    single { FirebaseStorage.getInstance() }

    // Repositories
    single { AuthRepository(get()) }

    // ViewModels
    viewModel { LoginViewModel(get(), get()) }
    viewModel { ProfileViewModel(get(), get()) }
    viewModel { OrganizationViewModel(get()) }
    viewModel { CampaignsViewModel(get(), get()) }
    viewModel { MapsViewModel(get()) }
}