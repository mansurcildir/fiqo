import Chart from 'react-apexcharts';
import ComponentCard from '../../components/common/ComponentCard';
import BarChartOne from '../../components/charts/bar/BarChartOne';
import PageMeta from '../../components/common/PageMeta';
import { ApexOptions } from 'apexcharts';
import { useEffect, useState } from 'react';
import { userAPI } from '../../service/user-service';
import { usageAPI } from '../../service/usage-service';
import { Usage } from '../../model/usage/Usage';
import { getDay } from '../../utils/utils';

export default function BarChart() {
  const [usages, setUsages] = useState<Usage[]>();
  const [series, setSeries] = useState<number[]>([0]);

  const options: ApexOptions = {
    colors: ['#465FFF'],
    chart: {
      fontFamily: 'Outfit, sans-serif',
      type: 'radialBar',
      height: 330,
      sparkline: {
        enabled: true
      }
    },
    plotOptions: {
      radialBar: {
        startAngle: -85,
        endAngle: 85,
        hollow: {
          size: '80%'
        },
        track: {
          background: '#E4E7EC',
          strokeWidth: '100%',
          margin: 5
        },
        dataLabels: {
          name: { show: false },
          value: {
            fontSize: '36px',
            fontWeight: '600',
            offsetY: -40,
            color: '#1D2939',
            formatter: (val) => val + '%'
          }
        }
      }
    },
    fill: {
      type: 'solid',
      colors: ['#465FFF']
    },
    stroke: { lineCap: 'round' },
    labels: ['Progress']
  };

  useEffect(() => {
    userAPI.getProfile().then((res) => {
      const info = res.data;

      let percent = 0;

      if (info.maxFileSize > 0) {
        percent = (info.totalFileSize / info.maxFileSize) * 100;
      }

      percent = Math.min(100, Math.max(0, percent));

      setSeries([percent]);
    });

    usageAPI.getUsages(2025).then((res) => {
      setUsages(res.data);
    });
  }, []);

  return (
    <div>
      <PageMeta title="fiqo | Stats" description="Stats Page" />

      <div className="mb-6 rounded-2xl border border-gray-200 bg-gray-100 dark:border-gray-800 dark:bg-white/[0.03]">
        <div className="shadow-default rounded-2xl bg-white px-5 pt-5 pb-11 sm:px-6 sm:pt-6 dark:bg-gray-900">
          <div className="flex justify-between">
            <div>
              <h3 className="text-lg font-semibold text-gray-800 dark:text-white/90">Total Storage</h3>
              <p className="text-theme-sm mt-1 text-gray-500 dark:text-gray-400">Percentage of usage</p>
            </div>
          </div>
          <div className="relative">
            <div className="max-h-[330px]" id="chartDarkStyle">
              <Chart key={series[0]} options={options} series={series} type="radialBar" height={330} />
            </div>
          </div>
        </div>
      </div>

      <div className="space-y-6">
        <ComponentCard title="Daily Bandwidth">
          <BarChartOne
            categories={usages?.map((u) => getDay(u.createdAt)) || []}
            data={usages?.map((u) => u.bandwidth) || []}
          />
        </ComponentCard>
      </div>
    </div>
  );
}
