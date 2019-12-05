using System;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;
using Xamarin.Essentials;
using System.IO;
using ReciPlease.Classes;
using System.Collections.Generic;
using SQLite;
using System.Text;
using System.Reflection;

namespace ReciPlease
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class SharePage : ContentPage
    {
        Recipe recipeToShare = null;
        List<Recipe> recipeListToShare = new List<Recipe>();
        List<Ingredient> ingredientsToShare = new List<Ingredient>();

        public SharePage()
        {
            InitializeComponent();
            btnSend.Clicked += BtnSend_Clicked;
        }

        public SharePage(Recipe recipe)
        {
            recipeToShare = recipe;
            recipeListToShare.Add(recipe);
            InitializeComponent();
            btnSend.Clicked += BtnSend_Clicked;
        }


        private async void BtnSend_Clicked(object sender, EventArgs e)
        {
            PrepareRecipeToSend();
            string fileName = recipeToShare.recipeName + ".txt";
            var file = Path.Combine(FileSystem.CacheDirectory, fileName);

            //create the file and add the recipe information to it.
            File.WriteAllText(file, recipeToShare.ToString());

            //append each ingredient to the file.
            foreach (Ingredient i in ingredientsToShare)
            {
                File.AppendAllText(file, i.ToString());
            }

            await Share.RequestAsync(new ShareFileRequest
            {
                Title = Title,
                File = new ShareFile(file)
            });
        }

        private async void BtnSendList_Clicked(object sender, EventArgs e) 
        {
            PrepareRecipeToSend();

            await Share.RequestAsync(new ShareFileRequest 
            {
                Title = Title,
                File = new ShareFile(CreateCsvFile(recipeListToShare, ingredientsToShare, "SharedRecipe.csv"))
            });
        }

        private void PrepareRecipeToSend()
        {
            ingredientsToShare.Clear();
            using (SQLiteConnection conn = new SQLiteConnection(App.FilePath))
            {
                foreach (Ingredient i in conn.Table<Ingredient>())
                {
                    if (i.RecipeID == recipeToShare.Id)
                    {
                        ingredientsToShare.Add(i);
                    }
                }
            }
        }


        public static string CreateCsvFile(List<Recipe> recipes, List<Ingredient> ingredients,string filepath)
        {
            var filePath = Path.Combine(FileSystem.CacheDirectory, filepath);

            if (File.Exists(filePath)) 
            {
                File.Delete(filePath);
            }

            try
            {
                using (System.IO.StreamWriter file = new System.IO.StreamWriter(filePath, true))
                {
                    foreach (Recipe r in recipes)
                    {
                        file.WriteLine(r.Id + "," + r.recipeName + "," + r.type.ToString());
                        foreach (Ingredient i in ingredients)
                        {
                            if (r.Id == i.RecipeID)
                            {
                                file.WriteLine(i.ingredientName + "," + i.amount + "," + i.measurement.ToString());
                            }
                        }
                    }
                    return filePath;
                }
            }
            catch (Exception ex)
            {
                throw new ApplicationException("This program threw an error:", ex);
            }
        }

    }
}