using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace ReciPlease
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class FilePage : ContentPage
    {
        public FilePage(string fileName)
        {
            InitializeComponent();
            SetIncomingFile(fileName);
        }

        public void SetIncomingFile(string fileName) 
        {
            FileName.Text = fileName;
            using (var reader = new StreamReader(fileName)) 
            {
                List<string> listA = new List<string>();
                List<string> listB = new List<string>();
                List<string> listC = new List<string>();
                while (!reader.EndOfStream) 
                {
                    var line = reader.ReadLine();
                    var values = line.Split(',');
                    listA.Add(values[0]);
                    listB.Add(values[1]);
                    listC.Add(values[2]);
                }
            }
        }
    }
}