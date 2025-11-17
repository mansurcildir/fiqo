import Chart from 'react-apexcharts';
import { ApexOptions } from 'apexcharts';

interface Props {
  categories: string[];
  data: number[];
}

export default function BarChartOne({ categories, data }: Props) {
  const options: ApexOptions = {
    colors: ['#465fff'],
    chart: {
      fontFamily: 'Outfit, sans-serif',
      type: 'bar',
      height: 180,
      toolbar: {
        show: false
      }
    },
    plotOptions: {
      bar: {
        horizontal: false,
        columnWidth: '39%',
        borderRadius: 5,
        borderRadiusApplication: 'end'
      }
    },
    dataLabels: {
      enabled: false
    },
    stroke: {
      show: true,
      width: 4,
      colors: ['transparent']
    },
    xaxis: {
      categories: categories,
      axisBorder: {
        show: false
      },
      axisTicks: {
        show: false
      }
    },
    legend: {
      show: true,
      position: 'top',
      horizontalAlign: 'left',
      fontFamily: 'Outfit'
    },
    yaxis: {
      title: {
        text: undefined
      }
    },
    grid: {
      yaxis: {
        lines: {
          show: true
        }
      }
    },
    fill: {
      opacity: 1
    },

    tooltip: {
      x: {
        show: false
      },
      y: {
        formatter: (val: number) => `${val}`
      }
    }
  };
  const series = [
    {
      name: 'Bytes',
      data: data
    }
  ];
  return (
    <div className="custom-scrollbar max-w-full overflow-x-auto">
      <div id="chartOne" className="min-w-[1000px]">
        <Chart options={options} series={series} type="bar" height={180} />
      </div>
    </div>
  );
}
