using System;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;
using Xamarin.Essentials;
using System.IO;
using SQLite;
using ReciPlease.Data;

namespace ReciPlease
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class OptionsPage : ContentPage
    {
        public OptionsPage()
        {
            InitializeComponent();
            btnSend.Clicked += BtnSend_Clicked;
        }


        private async void BtnSend_Clicked(object sender, EventArgs e)
        {
            var fn = "SharedRecipe.txt";
            var file = Path.Combine(FileSystem.CacheDirectory, fn);
            File.WriteAllText(file, "Hello World");

            await Share.RequestAsync(new ShareFileRequest
            {
                Title = Title,
                File = new ShareFile(file)
            });

            //await Share.RequestAsync(new ShareTextRequest
            //{
            //    Text = entShare.Text,
            //    Title = "Share!"
            //});
        }
    }
}