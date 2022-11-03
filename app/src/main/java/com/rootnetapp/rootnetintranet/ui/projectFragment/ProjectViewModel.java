package com.rootnetapp.rootnetintranet.ui.projectFragment;

import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.rootnetapp.rootnetintranet.ui.projectFragment.models.ProjectDataResult;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ProjectViewModel extends ViewModel {


   public ProjectDataResult loadData (){
      String data = Files.readAllBytes(Paths.get("com/rootnetapp/rootnetintranet/ui/projectFragment/models/MockData"));
      return new Gson().fromJson(data, ProjectDataResult.class);
   }
}