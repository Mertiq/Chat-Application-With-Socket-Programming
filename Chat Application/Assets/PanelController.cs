using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class PanelController : MonoBehaviour
{
    [SerializeField] GameObject[] panels;

    public void ShowPanel(string panelToShow)
    {
        foreach (GameObject panel in panels)
        {
            if (panel.name.Equals(panelToShow))
            {
                panel.SetActive(true);
            }
            else
            {
                panel.SetActive(false);
            }
        }
    }
}
