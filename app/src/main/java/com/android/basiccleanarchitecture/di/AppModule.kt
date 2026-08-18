package com.android.basiccleanarchitecture.di

import com.android.basiccleanarchitecture.data.api.FakeExplorerApi
import com.android.basiccleanarchitecture.data.mapper.FileItemMapper
import com.android.basiccleanarchitecture.data.repository.ExplorerRepositoryImpl
import com.android.basiccleanarchitecture.domain.repository.ExplorerRepository
import com.android.basiccleanarchitecture.ui.explorer.ExplorerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Network & API
    single { FakeExplorerApi() }
    single { FileItemMapper() }

    // Repositories
    single<ExplorerRepository> {
        ExplorerRepositoryImpl(api = get(), mapper = get())
    }

    // ViewModels
    viewModel {
        ExplorerViewModel(repository = get())
    }
}
